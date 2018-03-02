package com.game.Model;

import com.game.ts.net.bean.StatisticFlowBean;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.DecimalFormat;
import java.util.Vector;

/**
 * @author jianpeng.zhang
 * @since 2017/2/27.
 */
public class StatisticsTableModel extends DefaultTableModel
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JTable jTable;
    private Class<?>[] typeArray = {Object.class, Object.class, Object.class, Object.class, Object.class};
    private DecimalFormat df = new DecimalFormat("0.00");
    private DecimalFormat df2 = new DecimalFormat("0.000");

    public StatisticsTableModel(JTable jTables, Object[][] data, Object[] columnNames)
    {
        super(data, columnNames);
        this.jTable = jTables;
        showStatisticData(null);
    }

    /**
     * 显示在表格的数据。 添加的顺序要与 要与{@link #typeArray} 和 标题栏
     * {@link com.game.Data.DataProvider#statistics_header}对应
     */
    public void showStatisticData(StatisticFlowBean bean)
    {
        dataVector = parseStatisticFlowBean2Vector(bean);
        jTable.updateUI();
    }

    private Vector parseStatisticFlowBean2Vector(StatisticFlowBean bean)
    {
        if (bean == null)
        {
            bean = new StatisticFlowBean();
        }

        Vector<Object> v1 = new Vector<>();
        v1.add("Total Package");
        v1.add(bean.getUpTotalPackNum() + bean.getDownTotalPackNum());
        v1.add(bean.getUpTotalPackNum());
        v1.add(bean.getDownTotalPackNum());
        v1.add(getProportion(bean.getUpTotalPackNum(), bean.getDownTotalPackNum()));

        Vector<Object> v2 = new Vector<>();
        v2.add("Total Flow");
        v2.add(bean.getDownTotalFlow() + bean.getUpTotalFlow());
        v2.add(bean.getUpTotalFlow());
        v2.add(bean.getDownTotalFlow());
        v2.add(getProportion(bean.getUpTotalFlow(), bean.getDownTotalFlow()));

        Vector<Object> v3 = new Vector<>();
        v3.add("Avg Package / s");
        v3.add(df2.format(bean.getUpAvgPackNum() + bean.getDownAvgPackNum()));
        v3.add(df2.format(bean.getUpAvgPackNum()));
        v3.add(df2.format(bean.getDownAvgPackNum()));
        v3.add(getProportion(bean.getUpAvgPackNum(), bean.getDownAvgPackNum()));

        Vector<Object> v4 = new Vector<>();
        v4.add("Avg Flow / s");
        v4.add(df2.format(bean.getUpAvgFlowNum() + bean.getDownAvgFlowNum()));
        v4.add(df2.format(bean.getUpAvgFlowNum()));
        v4.add(df2.format(bean.getDownAvgFlowNum()));
        v4.add(getProportion(bean.getUpAvgFlowNum(), bean.getDownAvgFlowNum()));

        Vector<Vector> vector = new Vector<>();
        vector.add(v1);
        vector.add(v2);
        vector.add(v3);
        vector.add(v4);
        return vector;
    }

    /**
     * 返回比例的字符串
     */
    private String getProportion(double number1, double number2)
    {
        if (number1 == 0 || number2 == 0)
        {
            return number1 + " : " + number2;
        }
        else
        {
            double bigger = number1 > number2 ? number1 : number2;
            return df.format((number1 / bigger)) + " : " + df.format((number2 / bigger));
        }
    }

    public Class<?> getColumnClass(int c)
    {
        return typeArray[c];
    }

    @Override public boolean isCellEditable(int row, int column)
    {
        return false;
    }

}
