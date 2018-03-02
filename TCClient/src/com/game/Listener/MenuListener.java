package com.game.Listener;

import com.game.ClientUI.Client;
import com.game.ClientUI.dialog.ChooseFileDialog;
import com.game.ClientUI.dialog.FilterManagerDialog;
import com.game.ClientUI.dialog.SavePathChooseDialog;
import com.game.config.TCConfig;
import com.game.utils.PropertyConfigUtil;
import com.game.utils.language.LanguageMgr;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author jianpeng.zhang
 * @since 2017/3/3.
 */
public class MenuListener {
	private Client client;

	public MenuListener(Client client) {
		this.client = client;
	}

	public void listen() {

		client.getSaveOperator().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SavePathChooseDialog dialog = new SavePathChooseDialog(client.getFrame());
				dialog.setVisible(true);
			}
		});

		client.getLoadOperator().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ChooseFileDialog dialog = new ChooseFileDialog(client.getFrame());
				dialog.setVisible(true);
			}
		});

		client.getFilterManagerMenu().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FilterManagerDialog dialog = new FilterManagerDialog(client.getFrame(),
						LanguageMgr.getTranslation("TClient.Filter.Manager.Title"));
				dialog.setVisible(true);
			}
		});

        client.getReloadMenu().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // 初始化property配置
                PropertyConfigUtil.init(TCConfig.class.getPackage());
                // 初始化客户端的设置
                TCConfig.init();

                client.getFixedBtn().setText(TCConfig.getClientCfg().isFixed() ? "unfixed" : "fixed");
            }
        });

        if (TCConfig.getClientCfg().getColumn() != null)
        {
            final String[] columns = TCConfig.getClientCfg().getColumn();
            final String[] hideColumns = TCConfig.getClientCfg().getHideColumn() == null ? new String[]{} :
                    TCConfig.getClientCfg().getHideColumn();

            for (String column : columns)
            {
                JCheckBoxMenuItem item = new JCheckBoxMenuItem(column);
                item.addItemListener(new ItemListener() {
                    @Override public void itemStateChanged(ItemEvent e)
                    {
                        String col = ((JCheckBoxMenuItem) e.getItem()).getText();
                        //过滤掉初始化时触发的事件
                        if (client.getHideMenu().getMenuComponents().length == columns.length)
                        {
                            if (((JCheckBoxMenuItem) e.getItem()).isSelected())
                            {
                                client.hideColumn(col, true);
                            }
                            else
                            {
                                client.showColumn(col, true);
                            }
                        }
                    }
                });
                for (String hide : hideColumns)
                {
                    if (column.trim().equals(hide.trim()))
                    {
                        item.setSelected(true);
                        client.hideColumn(hide, false);
                    }
                }
                client.getHideMenu().add(item);
            }
        }

	}
}
