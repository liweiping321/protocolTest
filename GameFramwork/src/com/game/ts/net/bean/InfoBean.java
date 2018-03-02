package com.game.ts.net.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.TypeUtils;
import com.game.utils.InfoBeanUtils;
import com.game.utils.LogUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jianpeng.zhang
 * @since 2017/1/9.
 * 协议树的bean
 */
public class InfoBean
{
	public static final String TypeBase="Base";
	public static final String TypeObject="JsonObject";//不可编辑
	public static final String TypeArrayBase="JsonArrayBase";
	
	public static final String TypeArrayItem="JsonArrayItem";//不可编辑
	
	public static final String TypeArrayObject="JsonArrayObject";//不可编辑

    private String mode;
    private String value;
    private String desc;
    private List<InfoBean> child=new ArrayList<InfoBean>();
    
    private String type=TypeBase;
    
    private  Class<?> valueType;
   
 
    private boolean editable;

    public InfoBean()
    {
    }

    public InfoBean(String mode, Object value, String desc)
    {
        this.mode = mode;
        this.value = TypeUtils.castToString(value);
        this.desc = desc;
        this.valueType=InfoBeanUtils.getValueType(value);
    }
    
    public InfoBean(String mode,  Object value)
    {
        this.mode = mode;
        this.value = TypeUtils.castToString(value);
        this.valueType=InfoBeanUtils.getValueType(value);
    }


    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public List<InfoBean> getChild()
    {
        return child;
    }

    public void setChild(List<InfoBean> child)
    {
        this.child = child;
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
    /**
     * 检查设置的值类型是否正确
     * @param checkValue
     * @return
     */
	public String checkCurrentValueType(String checkValue){
		if(TypeArrayBase.equals(type)){
			try{
				return JSON.parseArray(checkValue, valueType).toString();	 
			}catch(Exception e ){
				LogUtils.error("修改数据类型错误 name="+mode+", value="+checkValue+",valueType="+valueType.getName());
				return value;
			} 
		}else if(TypeBase.equals(type)){
			try{
				return TypeUtils.castToString(TypeUtils.castToJavaBean(checkValue, valueType)); 
			}catch(Exception e ){
				LogUtils.error("修改数据类型错误 name="+mode+", value="+checkValue+",valueType="+valueType.getName());
				return value;
			} 
		}else{
			LogUtils.error("修改数据类型错误 name="+mode+", value="+value+",valueType="+valueType.getName()+"不能修改");
		}
		return value;
	}
	/**
	 * 递归所有的值类型是否正确
	 */
	public boolean checkAllValueType(){
		if(TypeBase.equals(type)){
			try{
				TypeUtils.castToJavaBean(value, valueType);
				return true;
			}catch(Exception e ){
				LogUtils.error("值类型错误 name="+mode+",value="+value+",valueType="+valueType.getName());
				return false;
			} 
		} 
		if(!CollectionUtils.isEmpty(child)){
			for(InfoBean infoBean:child){
				if(!infoBean.checkAllValueType()){
					return false;
				}
			}
		}
		
		return true;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public Class<?> getValueType() {
		return valueType;
	}

	public void setValueType(Class<?> valueType) {
		this.valueType = valueType;
	}
	
	private InfoBean getChildInfoBean(String key){
		for(InfoBean infoBean:child){
			if(key.equals(infoBean.mode)){
				return infoBean;
			}
		}
		return null;
	}
	/**
	 * 根据路径查找属性的值类型(找不到返回空)
	 */
    public Class<?> getValueType(String[] keys,int index){
    	InfoBean infoBean=  getChildInfoBean(keys[index]);
    	if(infoBean!=null){
    		if(keys.length<=(index+1)){
    			return infoBean.getValueType();
    		}else{
    			return infoBean.getValueType(keys, (++index));
    		}
    	}else{
    		return null;
    	}
    }
   
}
