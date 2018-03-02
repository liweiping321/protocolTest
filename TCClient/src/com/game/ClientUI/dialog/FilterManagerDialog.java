package com.game.ClientUI.dialog;

import com.game.Data.DataProvider;
import com.game.Data.FilterHistory;
import com.game.Data.SaveModule;
import com.game.Render.ListCellRender;
import com.game.Render.ListTransferHandler;
import com.game.TCClient;
import com.game.component.FilterStringPanel;
import com.game.utils.ComponentUtil;
import com.game.utils.language.LanguageMgr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.game.Data.SaveModule.FilterText;

/**
 * @author jianpeng.zhang
 * @since 2017/2/20.
 */
public class FilterManagerDialog extends JDialog implements ActionListener
{
    private JList validList;
    private JList deleteList;

    private JButton okBtn;
    private JButton cancelBtn;

    public FilterManagerDialog(JFrame f, String s) {
        super(f, s);
        setSize(715, 470);

        validList = new JList();
        validList.setDragEnabled(true);

        DefaultListModel<String> defaultListModel1 = new DefaultListModel<>();

        for (FilterStringPanel element : DataProvider.getInstance().getModuleData(SaveModule.FilterText, FilterStringPanel[].class))
        {
            defaultListModel1.addElement( element.getText());
        }
        validList.setModel(defaultListModel1);
        validList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        validList.setTransferHandler(new ListTransferHandler());
        validList.setCellRenderer(new ListCellRender(null));
        validList.addMouseListener(listMouseAdapter);

        deleteList = new JList();
        deleteList.setDragEnabled(true);
        DefaultListModel<String> jList1Model2 = new DefaultListModel<>();
        deleteList.setModel(jList1Model2);
        deleteList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        deleteList.setTransferHandler(new ListTransferHandler());
        deleteList.setCellRenderer(new ListCellRender(new Color(0xFF2011)));

        // jList1Model2.addElement("ljsdfn");
        // jList1Model2.addElement("fduewsd");
        // jList1Model2.addElement("lj235efsdfn");
        // jList1Model2.addElement("fd23uewsd");
        // jList1Model2.addElement("ljss223dfn");
        // jList1Model2.addElement("fduefwewsd");

        okBtn = ComponentUtil.createButton(LanguageMgr.getTranslation("TClient.Button.OK"), "");
        cancelBtn = ComponentUtil.createButton(LanguageMgr.getTranslation("TClient.Button.Cancel"), "");

        JLabel label = new JLabel(LanguageMgr.getTranslation("TClient.Label.Filter.Manager.Tip"));
        Font font = new Font("", Font.ITALIC, 16);
        label.setFont(font);

        setLayout(null);
        JScrollPane scrollPane1 = new JScrollPane();
        JScrollPane scrollPane2 = new JScrollPane();
        scrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane1.setViewportView(validList);
        scrollPane2.setViewportView(deleteList);
        add(scrollPane1);
        add(scrollPane2);
        add(label);
        add(okBtn);
        add(cancelBtn);

        scrollPane1.setBounds(15, 15, 330, 360);
        scrollPane2.setBounds(355, 15, 330, 360);
        okBtn.setBounds(470, 388, 90, 30);
        cancelBtn.setBounds(580, 388, 90, 30);
        label.setBounds(15, 385, 450, 30);

        okBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getWidth()) / 2,
                         (Toolkit.getDefaultToolkit().getScreenSize().height - getHeight()) / 2);
    }

    private MouseAdapter listMouseAdapter = new MouseAdapter()
    {
        @Override public void mouseClicked(MouseEvent e)
        {
            if (e.getClickCount() == 2)
            {
                EditStatementDialog dialog = new EditStatementDialog(new JFrame(), (JList) e.getSource(),
                                                                     ((JList) e.getSource()).getSelectedIndex());
                dialog.setVisible(true);
                dialog.setAlwaysOnTop(true);
            }
        }
    };


    @Override public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == okBtn)
        {
            DataProvider.getInstance().getModule(SaveModule.FilterText, FilterHistory.class).clearData();
            for (int i = 0; i < validList.getModel().getSize(); i++)
            {
                DataProvider.getInstance().addModuleData(SaveModule.FilterText, (validList.getModel().getElementAt(i)));
            }
            String currentStatement = TCClient.clientUI.getComboBox1().getEditor().getItem().toString();
            TCClient.clientUI.getComboBox1().setModel(new DefaultComboBoxModel<FilterStringPanel>(
                    DataProvider.getInstance().getModuleData(FilterText, FilterStringPanel[].class)));
            TCClient.clientUI.getComboBox1().getEditor().setItem(new FilterStringPanel(currentStatement));
            dispose();
        }
        else if (e.getSource() == cancelBtn)
        {
            dispose();
        }
    }

    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        FilterManagerDialog dialog = new FilterManagerDialog(frame, "dddd");
        dialog.setVisible(true);
    }
}

