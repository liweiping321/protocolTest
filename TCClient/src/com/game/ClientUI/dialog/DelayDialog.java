package com.game.ClientUI.dialog;

import com.game.Model.RowNumberTableModel;
import com.game.TCClient;
import com.game.ts.net.bean.WrapTsBean;
import com.game.util.LoggerUtil;
import com.game.utils.language.LanguageMgr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * @author jianpeng.zhang
 * @since 2017/2/14.
 */
public class DelayDialog extends AbstractOperatorDialog implements ActionListener
{
    private JSpinner spinner;

    public DelayDialog(Frame owner)
    {
        super(owner, "Set delay time");
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
    }


    @Override public Component getInputField()
    {
        spinner = new JSpinner();
        spinner.setBounds(15, 40, 220, 30);
        return spinner;
    }

    @Override public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == okButton)
        {
            StringBuilder sb = new StringBuilder();
            String action = String.format("DELY[%d]", Integer.parseInt(spinner.getValue().toString()));
            RowNumberTableModel tableModel = (RowNumberTableModel) (TCClient.clientUI.getTable().getModel());

            int actionCol = tableModel.findColumn("action");
            int beanCol = tableModel.findColumn("tsBean");
            int idCol = tableModel.findColumn("id");

            for (int row : TCClient.clientUI.getTable().getSelectedRows()) {
                Vector<Object> rowData = ((Vector) tableModel.getDataVector().get(row));
                rowData.set(actionCol, action);
                ((WrapTsBean) rowData.get(beanCol)).getTsBean().setAction(action);
                sb.append(rowData.get(idCol)).append(",");
            }

            TCClient.clientUI.getTable().updateUI();

            if (sb.length() > 0) {
                sb.delete(sb.length() - 1, sb.length());
                LoggerUtil.info(TCClient.clientUI,
                                String.format(LanguageMgr.getTranslation("TClient.Button.Delay.Log"), sb.toString()));
            }
            this.dispose();
        }
        else if (e.getSource() == cancelButton)
        {
            this.dispose();
        }
    }
}
