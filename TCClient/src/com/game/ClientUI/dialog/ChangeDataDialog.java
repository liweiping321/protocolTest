package com.game.ClientUI.dialog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.TypeUtils;
import com.game.Data.TableDateProvider;
import com.game.Model.CustomerTreeTableModel;
import com.game.Model.RowNumberTableModel;
import com.game.TCClient;
import com.game.express.eng.ExpressEngine;
import com.game.ts.net.bean.InfoBean;
import com.game.ts.net.bean.TSBean;
import com.game.ts.net.bean.WrapTsBean;
import com.game.util.LoggerUtil;
import com.game.utils.InfoBeanUtils;
import com.game.utils.LogUtils;
import com.game.utils.language.LanguageMgr;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * @author jianpeng.zhang
 * @since 2017/2/15.
 */
public class ChangeDataDialog extends AbstractOperatorDialog implements ActionListener
{
    private JTextField textField;

    public ChangeDataDialog(Frame owner)
    {
        super(owner, "change data", 500, 150);
        okButton.setBounds(310, 80, 75, 30);
        okButton.addActionListener(this);
        cancelButton.setBounds(400, 80, 75, 30);
        cancelButton.addActionListener(this);
        label.setText("示例：content.wwwww.aaaa=23fd");
    }

    @Override public Component getInputField()
    {
        textField = new JTextField();
        textField.setBounds(15, 40, 470, 30);
        textField.setBorder(new LineBorder(new Color(0x00A5FF), 2));
        return textField;
    }

    @Override public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == okButton)
        {

            RowNumberTableModel tableModel = (RowNumberTableModel) (TCClient.clientUI.getTable().getModel());

            String[] content = textField.getText().split("=");
            if (content.length != 2)
            {
                JOptionPane.showMessageDialog(null, LanguageMgr.getTranslation("TClient.JComboBox.Statement.Error"));
                return;
            }
            String path = content[0];
            String value = content[1];

            int beanCol = tableModel.findColumn("tsBean");
            int idCol = tableModel.findColumn("id");

            try
            {
                StringBuilder failList = new StringBuilder();
                StringBuilder errorList = new StringBuilder();
                StringBuilder sb = new StringBuilder();

                WrapTsBean selectBean = null;
                int selectRow = TCClient.clientUI.getTable().getSelectedRow();

                for (int row = 0; row < TCClient.clientUI.getTable().getRowCount(); row++) {

                    Vector<Object> rowData = (Vector) tableModel.getDataVector().get(row);
                    WrapTsBean wrapTsBean = (WrapTsBean) rowData.get(beanCol);

                    if (selectRow == row)
                    {
                        selectBean = wrapTsBean;
                    }

                    TSBean tsBean = wrapTsBean.getTsBean();

                    InfoBean infoBean= InfoBeanUtils.json2InfoBeans(JSON.toJSONString(tsBean));
                    String tempPath=path.replaceAll("\\[", "\\.\\[");
                    Class<?> valueType=infoBean.getValueType(tempPath.split("\\."), 0);

                    if (valueType == null)
                    {
                        failList.append(rowData.get(idCol)).append(",");
                        continue;
                    }

                    Object setValue = null;
                    try
                    {
                        setValue = TypeUtils.castToJavaBean(value, valueType);
                    }
                    catch (Exception e1)
                    {
                        errorList.append(rowData.get(idCol)).append(",");

                        e1.printStackTrace();
                        LogUtils.error(e1);
                        continue;
                    }

                    Object rootObject=JSON.toJSON(tsBean);
                    ExpressEngine.set(rootObject, path, setValue);
                    wrapTsBean.setTsBean(JSON.parseObject(rootObject.toString(), TSBean.class));
                    tableModel.getDataVector().set(row, TableDateProvider.tsBean2Vector(wrapTsBean));

                    sb.append(rowData.get(idCol)).append(",");
                }

                ((CustomerTreeTableModel) TCClient.clientUI.getTreetable().getTreeTableModel()).setBean(selectBean);

                if (selectBean == null || selectBean.getTsBean() == null)
                {
                    TCClient.clientUI.setXmlData(null);
                }
                else
                {
                    TCClient.clientUI.setXmlData(selectBean.getTsBean());
                }

                StringBuilder result = new StringBuilder();
                result.append(String.format(LanguageMgr.getTranslation("TClient.Button.ChangeValue.Log"),
                                            textField.getText()));

                if (sb.length() > 0) {
                    sb.delete(sb.length() - 1, sb.length());
                    result.append(String.format(LanguageMgr.getTranslation("TClient.Button.ChangeValue.Success.Log"),
                                                sb.toString()));
                    TCClient.clientUI.getTable().updateUI();
                }
                if (failList.length() > 0) {
                    failList.delete(failList.length() - 1, failList.length());
                    result.append(String.format(LanguageMgr.getTranslation("TClient.Button.ChangeValue.Node.Error.Log"),
                                                failList.toString()));
                }
                if (errorList.length() > 0) {
                    errorList.delete(errorList.length() - 1, errorList.length());
                    result.append(String.format(LanguageMgr.getTranslation("TClient.Button.ChangeValue.Type.Error.Log"),
                                                errorList.toString()));
                }


                LoggerUtil.info(TCClient.clientUI, result.toString());
                dispose();
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, LanguageMgr.getTranslation("TClient.JComboBox.Statement.Error"));
                LoggerUtil.error(TCClient.clientUI, e1.getMessage());
            }
        }
        else if (e.getSource() == cancelButton)
        {
            this.dispose();
        }
    }
}
