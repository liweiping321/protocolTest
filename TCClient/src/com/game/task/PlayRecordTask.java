package com.game.task;

import com.game.ClientUI.Client;
import com.game.Data.RecordInfo;
import com.game.TCClient;
import com.game.ts.net.bean.WrapTsBean;
import com.game.util.LoggerUtil;
import com.game.utils.LogUtils;
import com.game.utils.language.LanguageMgr;

import java.util.List;

/**
 * @author jianpeng.zhang
 * @since 2017/2/24.
 */
public class PlayRecordTask implements Runnable
{
    private List<WrapTsBean> recordList;
    private Client client;

    public PlayRecordTask(Client client, List<WrapTsBean> recordList)
    {
        this.client = client;
        this.recordList = recordList;
    }

    @Override public void run()
    {
        if (client == null)
        {
            LogUtils.error("client is null");
        }
        else
        {
            if (recordList != null && recordList.size() > 0)
            {
                for (WrapTsBean tsBean : recordList)
                {
                    while (RecordInfo.state != RecordInfo.RecordState.playing)
                    {
                        if (RecordInfo.state == RecordInfo.RecordState.pause)  //暂停播放时
                        {
                            try
                            {
                                 Thread.sleep(1000);
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                                LogUtils.error(e);
                            }
                        }
                        else if (RecordInfo.state == RecordInfo.RecordState.none)
                        {
                            return;
                        }
                    }
                    TCClient.connector.sendMsg(tsBean.getTsBean());
                    LoggerUtil.info(client, String.format(LanguageMgr.getTranslation("TClient.Replay.SendMessage.Log"),
                                                          tsBean.getTsBean().getId()));

                    // 设置为发送成功的
                    tsBean.setHasSend(true);
                    tsBean.setHasMark(false);
                    client.getTable().updateUI();
                    try
                    {
                        Thread.sleep(3000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                        LogUtils.error(e);
                    }
                }

                client.stopPlay();
            }
        }
    }
}
