package com.game.tc.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.game.player.BaseModule;
import com.game.scheduler.SecheduledPool;
import com.game.tc.TCClientInfo;
import com.game.tc.TCService;
import com.game.ts.net.bean.InfoBean;
import com.game.ts.net.bean.TSBean;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 测试模块
 * @author jason.lin
 *
 */
@SuppressWarnings("serial")
public class TestModule extends BaseModule<TCClientInfo>{

	public TestModule(TCClientInfo player) {
		super(player);
	}

	private int i = 0;
	/**
	 * 测试发包
	 */
	public void testSendPackage() {
		System.err.println("========");
		
		SecheduledPool.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				TSBean tsBean = new TSBean();
				tsBean.setId(TCService.getAutoIncId());
				tsBean.setTscCmd(0);
				tsBean.setLength(1000);
				tsBean.setCode("0x01");
				tsBean.setSubCode("0x00");
				tsBean.setResult(true);
				tsBean.setDirectType("up");
				tsBean.setContent(
						JSONObject.parseObject("{\"isChangeChannel\":true,\"other\":0,\"src\":\"Eg=\",\"version\":[\"dsf\", \"dsfsdf\"], \"wwwww\":{\"aaaa\":\"bbbb\", \"cc\":\"dd\"}}"));
				tsBean.setIp("IP");
				tsBean.setUserName("username");
				tsBean.setSessionId("1111111111");
				tsBean.setDesc("dddsfsfsdf");
				player.getSession().write(JSON.toJSONString(tsBean));
			}
		}, 0, 3000, TimeUnit.MILLISECONDS);
	}

	public String createContent()
	{
		InfoBean bean = new InfoBean("CSClientPkg_" + i, "", "");
		ArrayList<InfoBean> child = new ArrayList<>();
		for (int j = 0; j < 4; j++)
		{
			InfoBean bean1 = new InfoBean("bean_" + i + "-" + j, j + i + "", "sdkj_" + j);
			ArrayList<InfoBean> beans = new ArrayList<>();
			for (int k = 0; k < 2; k++)
			{
				InfoBean bean2 = new InfoBean("bean_" + i + "-" + j + "-" + k, j + i + k + "", "33s" + j + k);
				beans.add(bean2);
			}
			beans.add(new InfoBean("", "d33sf", ""));
			InfoBean bean2 = new InfoBean();
			ArrayList<InfoBean> been = new ArrayList<>();
			been.add(new InfoBean("skjdk", "dsf", "ds"));
			bean2.setChild(been);
			beans.add(bean2);

			bean1.setChild(beans);
			child.add(bean1);
		}
		bean.setChild(child);
		i++;
		return JSONObject.toJSONString(bean);
	}
}
