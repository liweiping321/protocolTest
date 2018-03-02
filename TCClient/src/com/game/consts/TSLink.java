package com.game.consts;

import com.game.config.TCConfig;

/**
 * @author jianpeng.zhang
 * @since 2017/1/18.
 */
public class TSLink {
	public static final String BASE_LINK = TCConfig.getTcWebUrl()+"/api";

	public static final String UP = BASE_LINK + "/up";

	public static final String DOWN = BASE_LINK + "/down";

	public static final String START = BASE_LINK + "/start";

	public static final String PAUSE = BASE_LINK + "/pause";

	public static final String STOP = BASE_LINK + "/stop";

	public static final String LISTEN = BASE_LINK + "/listenUrl";

	public static final String UPUNCONFIG = BASE_LINK + "/upUnConfig";

	public static final String FILTER = BASE_LINK + "/setFilter";
}
