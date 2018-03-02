package com.game.ClientUI;

import com.game.Data.TableDateProvider;
import com.game.Model.RowNumberTableModel;
import com.game.TCClient;
import com.game.config.TCConfig;
import com.game.ts.net.bean.InfoBean;
import com.game.ts.net.bean.WrapTsBean;
import com.game.utils.InfoBeanUtils;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static com.game.TCClient.clientUI;

/**
 * @author jianpeng.zhang
 * @since 2017/2/28.
 */
public class TreeTableMenu extends JPopupMenu implements ActionListener
{
    private JMenuItem parameterize;
    private JMenu editHistory;

    private HashMap<String, HashSet<String>> historyMap = new HashMap<>();

    public TreeTableMenu()
    {
        parameterize = new JMenuItem("参数化");
        editHistory = new JMenu("历史数据");

        parameterize.addActionListener(this);
        buildHistoryMenu(null);

        add(parameterize);
        add(editHistory);
    }

    private void buildHistoryMenu(String path)
    {
        editHistory.removeAll();

        if (historyMap.get(path) != null)
        {
            for (String value : historyMap.get(path))
            {
                JMenuItem menuItem = new JMenuItem(value);
                menuItem.addActionListener(this);
                editHistory.add(menuItem);
            }
        }
    }

    /**
     * @param path 修改参数的路径
     * @param value 修改前的值
     */
    public void addHistory(String path, String value)
    {
        if (!historyMap.containsKey(path))
        {
            historyMap.put(path, new HashSet<String>());

        }
        historyMap.get(path).add(value);
    }

    public void show(Component invoker, int x, int y, String path)
    {
        buildHistoryMenu(path);
        super.show(invoker, x, y);
    }



    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == parameterize)
        {
            DefaultMutableTreeTableNode treeTableNode = (DefaultMutableTreeTableNode) (clientUI.getTreetable())
                    .getPathForRow(clientUI.getTreetable().getSelectedRow()).getLastPathComponent();

            Object[] values = null;
            InfoBean bean = (InfoBean) treeTableNode.getUserObject();
            Class type = bean.getValueType();


            if (type == Long.class)
            {
                values = Arrays.copyOf(TCConfig.getClientCfg().getParametric().getNumbers(),
                                       TCConfig.getClientCfg().getParametric().getNumbers().length, Object[].class);
            }
            else if (type == BigDecimal.class)
            {
                values = Arrays.copyOf(TCConfig.getClientCfg().getParametric().getDecimals(),
                                       TCConfig.getClientCfg().getParametric().getDecimals().length, Object[].class);
            }
            else if (type == Boolean.class)
            {
                values = Arrays.copyOf(TCConfig.getClientCfg().getParametric().getBoos(),
                                       TCConfig.getClientCfg().getParametric().getBoos().length, Object[].class);
            }
            else if (type == String.class)
            {
                values = Arrays.copyOf(TCConfig.getClientCfg().getParametric().getStrings(),
                                       TCConfig.getClientCfg().getParametric().getStrings().length, Object[].class);
            }
            addParametric(values);
        }
        else
        {
            Object[] values = new Object[]{e.getActionCommand()};
            addParametric(values);
        }
    }

    private void addParametric(Object[] values)
    {
        DefaultMutableTreeTableNode treeTableNode = (DefaultMutableTreeTableNode) (clientUI.getTreetable())
                .getPathForRow(clientUI.getTreetable().getSelectedRow()).getLastPathComponent();

        InfoBean bean = (InfoBean) treeTableNode.getUserObject();

        while (treeTableNode.getParent() != null)
        {
            treeTableNode = (DefaultMutableTreeTableNode) treeTableNode.getParent();
        }

        if (values != null)
        {
            int selectRow = TCClient.clientUI.getTable().getSelectedRow();
            WrapTsBean wrapTsBean =
                    ((RowNumberTableModel) TCClient.clientUI.getTable().getModel()).getWrapBeanAt(selectRow);

            ArrayList<WrapTsBean> addList = new ArrayList<>();

            String originValue = bean.getValue();

            for (Object object : values)
            {
                bean.setValue(object.toString());
                WrapTsBean cloneBean = wrapTsBean.clone();
                cloneBean.getTsBean().setContent(InfoBeanUtils.beans2JsonString((InfoBean) treeTableNode.getUserObject()));
                addList.add(cloneBean);

            }
            bean.setValue(originValue);

            RowNumberTableModel tableModel = (RowNumberTableModel) TCClient.clientUI.getTable().getModel();
            for (int i = 0; i < addList.size(); i++)
            {
                tableModel.insertRow(selectRow + i + 1, TableDateProvider.tsBean2Vector(addList.get(i)));
            }
            // tableModel.getDateProvider().addAfter(addList, wrapTsBean);
        }
    }
}
