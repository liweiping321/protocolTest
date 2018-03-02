package com.game.Data;

import com.alibaba.fastjson.JSON;
import com.game.TCClient;
import com.game.express.eng.ExpressEngine;
import com.game.ts.net.bean.TSBean;
import com.game.ts.net.bean.WrapTsBean;
import com.game.util.LoggerUtil;
import com.game.utils.language.LanguageMgr;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author jianpeng.zhang
 * @since 2017/2/9.
 */
public class TableDateProvider {
	/**
	 * 显示所有数据
	 */
	public static final int ALL_DATA = 1;
	/**
	 * 显示所有标记的数据
	 */
	public static final int MARK_DATA = 2;
	/**
	 * 显示所有发送的数据
	 */
	public static final int SEND_DATA = 3;
	/**
	 * 显示所有删除的数据
	 */
	public static final int DEL_DATA = 4;
	/**
	 * 显示所有上行的数据
	 */
	public static final int UP_DATA = 5;
	/**
	 * 显示所有下行的数据
	 */
	public static final int DOWN_DATA = 6;
	/**
	 * 表示单前有应用条件
	 */
	public static final int FILTERING = 7;
	/**
	 * 其他情况
	 */
	public static final int OTHER = 8;

	/**
	 * 单前显示的表格的筛选类型
	 */
	private static int state = ALL_DATA;

	private CopyOnWriteArrayList<WrapTsBean> allData = new CopyOnWriteArrayList<>();

	private List<WrapTsBean> removeData = new ArrayList<>();

	public void setState(int state) {
		TableDateProvider.state = state;
	}

	/**
	 * @return 当前状态是否可以添加服务端发过来的数据
	 */
	public boolean canShowData(TSBean tsBean) {
		if (state == ALL_DATA || state == OTHER) {
			return true;
		} else if (state == UP_DATA) {
			return "up".equals(tsBean.getDirectType().trim().toLowerCase());
		} else if (state == DOWN_DATA) {
			return "down".equals(tsBean.getDirectType().trim().toLowerCase());
		}
		return false;
	}

	/**
	 * 判断能不能进行操作，可能要限制丢弃面板的操作
	 */
	public static boolean canOperate() {
		if (state == DEL_DATA) {
			LoggerUtil.warn(TCClient.clientUI, LanguageMgr.getTranslation("TClient.Del.Model.Warn"));
			return false;
		} else if (RecordInfo.state == RecordInfo.RecordState.playing) {
			LoggerUtil.warn(TCClient.clientUI, LanguageMgr.getTranslation("TClient.RePlay.Model.Warn"));

		}
		return true;
	}

	public Vector getConditionResult(String conditionStr) {
		Vector<Vector> filterData = new Vector<>();

		for (WrapTsBean tsBean : allData) {
			if (ExpressEngine.match(JSON.toJSON(tsBean.getTsBean()), conditionStr)) {
				filterData.add(tsBean2Vector(tsBean));
			}
		}
		return filterData;
	}

	public void swap(WrapTsBean bean1, WrapTsBean bean2)
	{
		if (bean1 != null && bean2 != null)
		{
			int index1 = allData.indexOf(bean1);
			int index2 = allData.indexOf(bean2);
			if (index1 >= 0 && index2 >= 0)
			{
				allData.remove(index1);
				allData.add(index1, bean2);
				allData.remove(index2);
				allData.add(index2, bean1);
			}
		}
	}
	public Vector add(WrapTsBean tsBean) {
		if (tsBean != null) {
			allData.add(tsBean);
			return tsBean2Vector(tsBean);
		}
		return null;
	}

	public void addAll(List<WrapTsBean> tsBean) {
		if (tsBean != null) {
			allData.addAll(tsBean);
		}
	}

	public void addAfter(WrapTsBean addBean, WrapTsBean afterBean) {
		if (addBean != null && afterBean != null) {
			allData.add(allData.indexOf(afterBean) + 1, addBean);
		}
	}

	public void addAfter(List<WrapTsBean> addBeanList, WrapTsBean afterBean) {
		if (addBeanList != null && afterBean != null) {
			allData.addAll(allData.indexOf(afterBean) + 1, addBeanList);
		}
	}

	public void cut(List<WrapTsBean> newList, List<WrapTsBean> cutList, WrapTsBean pasteAt) {
		if (newList != null && cutList != null && pasteAt != null) {
			allData.removeAll(cutList);
			allData.addAll(allData.indexOf(pasteAt) + 1, newList);
		}
	}

	public void removeAll() {
		removeData.addAll(allData);
		allData.clear();
	}

	public void remove(List<WrapTsBean> removeList) {
		if (removeList != null) {
			removeData.addAll(removeList);
			allData.removeAll(removeList);
		}
	}

	public ArrayList<WrapTsBean> getMarkData() {
		ArrayList<WrapTsBean> result = new ArrayList<>();
		for (WrapTsBean bean : allData) {
			if (bean.isHasMark()) {
				result.add(bean);
			}
		}
		return result;
	}

	public ArrayList<WrapTsBean> getNoSendData() {
		ArrayList<WrapTsBean> result = new ArrayList<>();
		for (WrapTsBean bean : allData) {
			if (!bean.isHasSend()) {
				result.add(bean);
			}
		}
		return result;
	}

	public Vector getAllTypeList() {
		Vector<Object> dataVector = new Vector<>();
		for (WrapTsBean bean : allData) {
			dataVector.add(tsBean2Vector(bean));
		}
		return dataVector;
	}

	public Vector getSendTypeList() {
		Vector<Object> dataVector = new Vector<>();
		for (WrapTsBean bean : allData) {
			if (bean.isHasSend()) {
				dataVector.add(tsBean2Vector(bean));
			}
		}
		return dataVector;
	}

	public Vector getMarkTypeList() {
		Vector<Object> dataVector = new Vector<>();
		for (WrapTsBean bean : allData) {
			if (bean.isHasMark()) {
				dataVector.add(tsBean2Vector(bean));
			}
		}
		return dataVector;
	}

	public Vector getDelTypeList() {
		Vector<Object> dataVector = new Vector<>();
		for (WrapTsBean bean : removeData) {
			dataVector.add(tsBean2Vector(bean));
		}
		return dataVector;
	}

	public Vector getUpTypeList() {
		Vector<Object> dataVector = new Vector<>();
		for (WrapTsBean bean : allData) {
			if ("up".equals(bean.getTsBean().getDirectType().trim().toLowerCase())) {
				dataVector.add(tsBean2Vector(bean));
			}
		}
		return dataVector;
	}

	public Vector getDownTypeList() {
		Vector<Object> dataVector = new Vector<>();
		for (WrapTsBean bean : allData) {
			if ("down".equals(bean.getTsBean().getDirectType().trim().toLowerCase())) {
				dataVector.add(tsBean2Vector(bean));
			}
		}
		return dataVector;
	}

	public CopyOnWriteArrayList<WrapTsBean> getAllData() {
		return allData;
	}

	public static Vector tsBean2Vector(WrapTsBean wrapTsBean) {
		TSBean tsBean = wrapTsBean.getTsBean();
		Vector<Object> vector = new Vector<>();
		vector.add("");
		vector.add("");
		vector.add(tsBean.getId());
		vector.add(tsBean.getDirectType());
		vector.add(tsBean.getIp());
		vector.add(tsBean.getUserName());
		vector.add(tsBean.getLength());
		vector.add(tsBean.getCode());
		vector.add(tsBean.getSubCode());
		vector.add(tsBean.isConfig() ? "true" : "false");
		vector.add(tsBean.getDesc());
		vector.add(wrapTsBean);
		return vector;
	}

}
