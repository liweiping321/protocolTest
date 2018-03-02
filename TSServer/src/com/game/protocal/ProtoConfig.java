package com.game.protocal;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import com.game.TSServer;
import com.game.type.ProtocolType;
import com.game.utils.LogUtils;
import com.game.utils.StringUtil;
import com.game.utils.XMLUtil;

/**
 * 协议配置
 * 
 * @author jason.lin
 *
 */
public class ProtoConfig {
	// 玩家登录协议标识
	private String loginName;

	// 上行 下行
	private String type;

	// 指令号
	private String code;

	// 子指令号
	private String subCode="0";

	// 指令描述
	private String desc;

	// 协议类型
	private String protocolType;

	// 消息体类名
	private String bodyClazzName;

	// pb解析类
	private Class<?> bodyClass;

	// 二进制属性配置
	private BinConfig binConfig;
	
	// 更改登录秘钥
	private boolean updateKey;
	
	// 是否可能压缩
	private boolean compress;
	//是否需要过滤
	private boolean filter;
	
	// pb初始化方法
	private Method pbInitMethod;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSubCode() {
		return subCode;
	}

	public void setSubCode(String subCode) {
		this.subCode = subCode;
	}

	/**
	 * 判定进制
	 * @param cmd
	 * @return
	 */
	public static int getRadix(String cmd){
		return cmd.toLowerCase().startsWith("0x") ? 16 : 10 ;
	}
	
	public short getCodeShort(){
		return toRadixShort(code);
	}
	
	public short getSubCodeShort(){
		return toRadixShort(subCode);
	}
	
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getBodyClazzName() {
		return bodyClazzName;
	}

	public void setBodyClazzName(String bodyClazzName) {
		this.bodyClazzName = bodyClazzName;
	}

	public Class<?> getBodyClass() {
		return bodyClass;
	}

	public void setBodyClass(Class<?> bodyClass) {
		this.bodyClass = bodyClass;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProtocolType() {
		return protocolType;
	}

	public void setProtocolType(String protocolType) {
		this.protocolType = protocolType;
	}

	/**
	 * 初始化解析类
	 * @param elementMap
	 */
	public void initCoderClass(Map<String, Element> elementMap) {
		try {
			switch (ProtocolType.valueOf(protocolType)) {
			case PB:// protobuffer类型
				if (StringUtil.isEmpty(bodyClazzName)) {
					return;
				}
				try {
					Class<?> T = Class.forName(bodyClazzName);
					Method method = T.getMethod("newBuilder");
					this.setBodyClass(T);
					this.setPbInitMethod(method);
				} catch (ClassNotFoundException e) {
					LogUtils.error("init pb bodyClazzName:" + bodyClazzName, e);
				}
				break;

			case BIN_FILE:// 二进制类型
				if (StringUtil.isEmpty(bodyClazzName)) {
					return;
				}
				
				String filePath = TSServer.getGameBasePath() + "/proto/" + bodyClazzName;
				List<BinProperty> list = XMLUtil.fromMulXmlToList(BinProperty.class, filePath, "protocol");
				BinConfig config = new BinConfig();
				config.setList(list);
				this.setBinConfig(config);
				break;
			case BIN:// 二进制类型
				if(elementMap == null || elementMap.size() == 0){
					LogUtils.error("empty elementMap!");
					break;
				}
				
//			String key = type.toLowerCase() + "_" + code +"_" + subCode;
				String key = getBodyClazzName();
				config = new BinConfig();
				this.setBinConfig(config);
				if(StringUtil.isEmpty(key)){
					break;
				}
				
				Element element = elementMap.get(getBodyClazzName());
				if(element == null){
					LogUtils.error("BinConfig文件中找不到 key:" + key);
					break;
				}
				
				list = XMLUtil.fromMulElementToList(BinProperty.class, element);
				config.setList(list);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			LogUtils.error("initCoderClass error", e);
		} 
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public BinConfig getBinConfig() {
		return binConfig;
	}

	public void setBinConfig(BinConfig binConfig) {
		this.binConfig = binConfig;
	}

	public static short toRadixShort(String cmd){
		int radix = getRadix(cmd);
		if(radix == 10){
			return Short.valueOf(cmd);
		}
		
		return Short.valueOf(cmd.substring(2), radix);
	}
	
	public boolean getUpdateKey() {
		return updateKey;
	}

	public void setUpdateKey(boolean updateKey) {
		this.updateKey = updateKey;
	}

	public boolean isCompress() {
		return compress;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
	}

	public boolean isFilter() {
		return filter;
	}

	public void setFilter(boolean filter) {
		this.filter = filter;
	}

	public Method getPbInitMethod() {
		return pbInitMethod;
	}

	public void setPbInitMethod(Method pbInitMethod) {
		this.pbInitMethod = pbInitMethod;
	}
}
