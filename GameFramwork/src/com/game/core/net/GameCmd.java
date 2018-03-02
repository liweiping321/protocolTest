package com.game.core.net;

import com.game.player.obj.BaseOnlinePlayer;

/**
 * cmd指令
 * @author jason.lin
 *
 */
public abstract class GameCmd extends Cmd{

	public abstract void execute(BaseOnlinePlayer player, Request request)
			throws Exception;
}
