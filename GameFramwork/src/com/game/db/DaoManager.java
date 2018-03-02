package com.game.db;

import com.game.db.database.DBPoolManager;
import com.game.db.database.IBaseDao;
import com.game.db.database.pool.DBHelper;

import java.util.HashMap;
import java.util.Map;


public class DaoManager {
    /**
     * 游戏主库hepler
     */
    public static final DBHelper dbHelper = DBPoolManager.getDBHelper(ConnStrategy.getMainDB());
    /**
     * 日志库hepler
     */
    public static final DBHelper logDbHelper = DBPoolManager.getDBHelper(ConnStrategy.getLogDB());

    public static Map<Class<?>, IBaseDao<?>> daoMapping = new HashMap<Class<?>, IBaseDao<?>>();


    public static void addDao(IBaseDao<?> clazz){
        daoMapping.put(clazz.getClass(), clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getMappingDao(Class<T> T) {
        return (T)daoMapping.get(T);
    }


}
