package com.game.scheduler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import com.game.cache.Cache;
import com.game.protocal.ProtoConfig;
import com.game.utils.DataUtil;
import com.game.utils.LogUtils;


/**
 * jar加载任务
 * @author jason.lin
 *
 */
public class JarLoadJob extends BaseWatchJob{

	public JarLoadJob(String loadPath) {
		super(loadPath);
	}

	@SuppressWarnings({ "rawtypes", "deprecation" })
	@Override
	public void excute(String path) {
		if(!path.endsWith(".jar")){
			return;
		}
		
		URLClassLoader loader = null;
		try {
			File file = new File(path);
			URL url = file.toURL();
			loader = new URLClassLoader(new URL[]{url});
			
			List<ProtoConfig> list = Cache.getProtoConfigList();
			if(DataUtil.isEmpty(list)){
				return;
			}
			
			for(ProtoConfig config : list){
				Class bodyClass = loader.loadClass("com.game.bean."+config.getBodyClazzName());
				config.setBodyClass(bodyClass);
			}
			
		} catch (Exception e) {
			LogUtils.error("JarLoadJob", e);
		}finally {
			if(loader != null){
				try {
					loader.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
