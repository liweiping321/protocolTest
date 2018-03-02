package com.game.db.database;

/**
 * Date: 2013-5-24
 * 
 * Copyright (C) 2013-2015 7Road. All rights reserved.
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.db.DaoManager;

/**
 * @author ewing.wang
 */
public class DataSaveInfo<T> extends DataObject {
	
	@Override
	public String toString() {
		if (this.getOp() == DataOption.INSERT){
			return "DataSaveInfo [claz=" + claz + "  " + set.size() + "]";
		}
		
		return "DataSaveInfo [claz=" + claz + "  " + list.size() + "]";
	}

	// 日志记录器
	private static Logger logger = LoggerFactory.getLogger(DataSaveInfo.class
			.getName());
	private Class<T> claz;
	private Set<T> set;
	private List<T> list;

	public DataSaveInfo(Class<T> claz) {
		this.set = new HashSet<T>();
		this.list = new ArrayList<T>();
		this.claz = claz;
	}

	public void prepareStatement() {
		@SuppressWarnings("unchecked")
		IBaseDao<T> dao = (IBaseDao<T>) DaoManager.getMappingDao(this.claz);
		if (dao == null) {
			logger.error("cann't find @DaoSaveInfoAnnotation config in com.road.bll.DaoManage---- "
							+ claz, new NullPointerException());
			return;
		}

		try {
			if (this.getOp() == DataOption.INSERT) {
				dao.addOrUpdateBatch(set);
			} else if (this.getOp() == DataOption.UPDATE) {
				dao.addOrUpdateBatch(list);
			}
		} catch (Throwable e) {
			logger.error(String.format("save into db error--->%s table ", this.claz), e);
		}
	}

	public Class<?> getClaz() {
		return claz;
	}

	public void setClaz(Class<T> claz) {
		this.claz = claz;
	}

	public Set<T> getSet() {
		return set;
	}

	public void setSet(Set<T> set) {
		this.set = set;
	}

	public List<T> getList() {
		return list;
	}
}
