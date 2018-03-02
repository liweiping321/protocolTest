package com.game.Listener;

import com.game.ClientUI.Client;
import com.game.ClientUI.dialog.CreateStatementDialog;
import com.game.Data.TableDateProvider;
import com.game.style.FilterBoxEditor;
import com.game.utils.StringUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author jianpeng.zhang
 * @since 2017/1/9.
 */
public class ButtonListener {
	private Client client;

	public ButtonListener(Client client) {
		this.client = client;
	}

	public void listen() {
		client.getSSBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (TableDateProvider.canOperate()) {
					client.sendSelectItem();
				}
			}
		});

		client.getSMBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (TableDateProvider.canOperate()) {
					client.sendMarkItem();
				}
			}
		});

		client.getSABtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (TableDateProvider.canOperate()) {
					client.markItem();
				}
			}
		});

		client.getSNBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (TableDateProvider.canOperate()) {
					client.sendNotSendItem();
				}
			}
		});

		client.getCSBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (TableDateProvider.canOperate()) {
					client.cancelMarkItem();
				}
			}
		});

		client.getDABtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (TableDateProvider.canOperate()) {
					client.deleteAllItem();
				}
			}
		});

		client.getListenBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				client.listen(false);
			}
		});

		client.getClearListenBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				client.cancelListen();
			}
		});

		client.getReceiveBinaryBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				client.receiveBinary(client.getReceiveBinaryBtn().getIcon() == null);
			}
		});

		client.getUpBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				client.up(client.getUpBtn().getIcon() == null);
			}
		});

		client.getDownBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				client.down(client.getDownBtn().getIcon() == null);
			}
		});

		client.getStartBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				client.start();
			}
		});

		client.getPauseBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				client.pause();
			}
		});

		client.getStopBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				client.stop();
			}
		});

		client.getReplayBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				client.replay();
			}
		});

		client.getPausePlayBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				client.pausePlay();
			}
		});

		client.getStopPlayBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				client.stopPlay();
			}
		});

		client.getReConnectBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				client.connect();
			}
		});

		client.getClearFilterBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!StringUtil.isEmpty(client.getComboBox1().getEditor().getItem().toString())) {
					client.filterWithCondition("");
				}
			}
		});

		client.getEditBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CreateStatementDialog dialog = new CreateStatementDialog(client.getFrame(), client.getComboBox1());
				dialog.setVisible(true);
			}
		});

		client.getFilterBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String conditionStr = client.getComboBox1().getEditor().getItem().toString();
				if (!client.filterWithCondition(conditionStr)) {
					((FilterBoxEditor) client.getComboBox1().getEditor()).setError();
				}
			}
		});

		client.getMoveUpBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (TableDateProvider.canOperate()) {
					client.moveUp();
				}
			}
		});

		client.getMoveDownBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (TableDateProvider.canOperate()) {
					client.moveDown();
				}
			}
		});

		client.getAimBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (TableDateProvider.canOperate()) {
					client.aimAt();
				}
			}
		});

		client.getFixedBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				client.setFixed(client.getFixedBtn().getText().equals("fixed"));
			}
		});

		client.getFindBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				client.setFindCondition();
			}
		});

	}
}
