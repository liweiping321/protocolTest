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
public class RepeatDialog extends AbstractOperatorDialog implements ActionListener
{
    private JSpinner spinner;

    public RepeatDialog(Frame owner)
    {
        super(owner, "Set repeat times");
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        label.setText("Repeat Times");
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
            RowNumberTableModel tableModel = (RowNumberTableModel) (TCClient.clientUI.getTable().getModel());
            String action = String.format("REP[%d]", Integer.parseInt(spinner.getValue().toString()));

            int actionCol = tableModel.findColumn("action");
            int beanCol = tableModel.findColumn("tsBean");
            int idCol = tableModel.findColumn("id");

            for (int row : TCClient.clientUI.getTable().getSelectedRows()) {
                Vector<Object> rowData = (Vector) tableModel.getDataVector().get(row);
                rowData.set(actionCol, action);
                ((WrapTsBean) rowData.get(beanCol)).getTsBean().setAction(action);
                sb.append(rowData.get(idCol)).append(",");
            }
            TCClient.clientUI.getTable().updateUI();

            if (sb.length() > 0) {
                sb.delete(sb.length() - 1, sb.length());
                LoggerUtil.info(TCClient.clientUI,
                                String.format(LanguageMgr.getTranslation("TClient.Button.Repeat.Log"), sb.toString()));
            }
            dispose();
        }
        else if (e.getSource() == cancelButton)
        {
            this.dispose();
        }
    }
}
