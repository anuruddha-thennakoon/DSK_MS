/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

import static interfaces.Home.jDesktopPane1;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author ANURUDDHA
 */
public class UpdateStock extends javax.swing.JInternalFrame {

    static DecimalFormat df = new DecimalFormat("00.00");
    public static String bank = "";
    public static String cheque_no;
    public static String date;

    /**
     * Creates new form UpdateStock
     */
    public UpdateStock() {
        initComponents();
        itemCombo();
        supplier();
    }

    void combo() {
        try {
            ResultSet rs = JDBC.getdata("SELECT * FROM items WHERE name LIKE '" + jTinvoice_no1.getText() + "%' AND status='Active'");
            Vector v = new Vector();
            while (rs.next()) {
                v.add(rs.getString("name"));
            }
            jCGRN_item.setModel(new DefaultComboBoxModel(v));
            jCGRN_item.showPopup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void supplier() {
        try {
            ResultSet rs = JDBC.getdata("SELECT * FROM supplier WHERE status='Active'");
            Vector v = new Vector();
            while (rs.next()) {
                v.add(rs.getString("supplier_combo"));
            }
            jCGRN_supplier.setModel(new DefaultComboBoxModel(v));
        } catch (Exception e) {
        }
    }

    void itemCombo() {
        try {
            ResultSet rs = JDBC.getdata("SELECT * FROM items WHERE status='Active'");
            Vector v = new Vector();
            while (rs.next()) {
                v.add(rs.getString("item_combo"));
            }
            jCGRN_item.setModel(new DefaultComboBoxModel(v));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void addTable() {
        String iid = null;
        String iname = null;
        try {
            ResultSet rs = JDBC.getdata("SELECT * FROM items WHERE name='" + jTinvoice_no1.getText().toString().trim() + "'");
            while (rs.next()) {
                iid = rs.getString("iid");
                iname = rs.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        DefaultTableModel d = (DefaultTableModel) jTable3.getModel();
        Vector v = new Vector();
        v.add(iid);
        v.add(iname);
        v.add(jTGRN_up.getText());
        v.add(jTGRN_qty.getText());
        v.add(jTGRN_net.getText());
        d.addRow(v);
        jTGRN_qty.setText("");
        jTGRN_up.setText("");
        jTGRN_net.setText("");
        jTinvoice_no1.setText("");
    }

    public boolean checkDuplicate(int columnIndex, JTable jt, String iid) {
        DefaultTableModel d = (DefaultTableModel) jTable3.getModel();
        int rowCount = d.getRowCount();
        boolean b = false;
        for (int x = 0; x < rowCount; x++) {
            String tiid = jt.getValueAt(x, columnIndex).toString();
            if (iid.equals(tiid)) {
                b = true;
            }
        }
        return b;
    }

    void GRN_item_count() {
        try {
            DefaultTableModel d = (DefaultTableModel) jTable3.getModel();
            double item_count = 0;
            double sub_total = 0;
            for (int x = 0; x < d.getRowCount(); x++) {
                item_count = item_count + Double.parseDouble(jTable3.getValueAt(x, 3).toString());
                sub_total = sub_total + Double.parseDouble(jTable3.getValueAt(x, 4).toString());
            }
            jTItem_count.setText(df.format(item_count));
            jTNet_amount.setText(df.format(sub_total));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    void textfieldVisible() {
//        if (jCCash.isSelected()) {
//            jTCash_amount.setEnabled(true);
//            setZero();
//        } else {
//            jTCash_amount.setEnabled(false);
//            setZero();
//        }
//        if (jCCheque.isSelected()) {
//            jTCheque_amount.setEnabled(true);
//            setZero();
//        } else {
//            jTCheque_amount.setEnabled(false);
//            setZero();
//        }
//    }
//    public static boolean cal() {
//        boolean bool = false;
//        double na = Double.parseDouble(jTNet_amount.getText().trim());
//        double ca = Double.parseDouble(jTCash_amount.getText().trim());
//        double cha = Double.parseDouble(jTCheque_amount.getText().trim());
//
//        if (jCCash.isSelected() && !(jCCheque.isSelected()) && !(jCCredit.isSelected())) {
//            if (ca < na) {
//                JOptionPane.showMessageDialog(null, "You have selected the cash option,can not enter cash amount lesser than net amount", "Input error", JOptionPane.ERROR_MESSAGE);
//                bool = true;
//                setZero();
//            } else {
//                jTBalance.setForeground(Color.black);
//                jTBalance.setText(df.format(ca - na));
//            }
//        } else if (jCCheque.isSelected() && !(jCCash.isSelected()) && !(jCCredit.isSelected())) {
//            if (!(cha == na)) {
//                JOptionPane.showMessageDialog(null, "You have selected the cheque option,enter cheque amount equal to net amount", "Input error", JOptionPane.ERROR_MESSAGE);
//                bool = true;
//                setZero();
//            } else {
//                jTBalance.setForeground(Color.black);
//                jTBalance.setText(df.format(cha - na));
//            }
//        } else if (jCCredit.isSelected() && !(jCCheque.isSelected()) && !(jCCredit.isSelected())) {
//            //
//        } else if (jCCash.isSelected() && jCCredit.isSelected() && !(jCCheque.isSelected())) {
//            jTBalance.setForeground(Color.red);
//            jTBalance.setText(df.format(-1 * (ca - na)));
//        } else if (jCCash.isSelected() && jCCheque.isSelected() && !(jCCredit.isSelected())) {
//            if (!((cha + ca) == na)) {
//                if (na < cha) {
//                    JOptionPane.showMessageDialog(null, "You have selected the cash and cheque option,enter correct amounts ", "Input error", JOptionPane.ERROR_MESSAGE);
//                    bool = true;
//                    setZero();
//                } else if ((cha + ca) < na) {
//                    JOptionPane.showMessageDialog(null, "You have selected the cash and cheque option,enter correct amounts ", "Input error", JOptionPane.ERROR_MESSAGE);
//                    bool = true;
//                    setZero();
//                } else {
//                    jTBalance.setForeground(Color.black);
//                    double d = ca - (na - cha);
//                    System.out.println("asd");
//                    jTBalance.setText(df.format(d));
//                    System.out.println("cvb");
//                }
//            } else {
//                jTBalance.setForeground(Color.black);
//                double d = ca - (na - cha);
//                System.out.println("asd");
//                jTBalance.setText(df.format(d));
//                System.out.println("cvb");
//            }
//        } else if (jCCredit.isSelected() && jCCheque.isSelected() && !(jCCash.isSelected())) {
//            if (na <= cha) {
//                JOptionPane.showMessageDialog(null, "You have selected the credit and cheque option,enter correct amounts ", "Input error", JOptionPane.ERROR_MESSAGE);
//                bool = true;
//                setZero();
//            } else {
//                jTBalance.setForeground(Color.red);
//                jTBalance.setText(df.format(-1 * (cha - na)));
//            }
//        } else if (jCCredit.isSelected() && jCCheque.isSelected() && jCCash.isSelected()) {
//            jTBalance.setForeground(Color.red);
//            jTBalance.setText(df.format(-1 * ((ca + cha) - na)));
//        } else if (!(jCCash.isSelected()) && !(jCCheque.isSelected()) && !(jCCredit.isSelected())) {
//            jTBalance.setForeground(Color.black);
//            jTBalance.setText("00.00");
//        }
//        return bool;
//    }
//    static void setZero() {
//        jTBalance.setForeground(Color.black);
//        jTCash_amount.setText("00.00");
//        jTCheque_amount.setText("00.00");
//        jTBalance.setText("00.00");
//    }
    public static void toDB() {
        try {
            DefaultTableModel d = (DefaultTableModel) jTable3.getModel();
            if (d.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No items added to save", "Input error", JOptionPane.ERROR_MESSAGE);
            } else {
                if (jTinvoice_no.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter invoice number", "Input error", JOptionPane.ERROR_MESSAGE);
                } else if (jCGRN_supplier.getSelectedItem().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please select supplier", "Input error", JOptionPane.ERROR_MESSAGE);
                } else {
                    int i = JOptionPane.showConfirmDialog(null, "Are you sure you want to save?", "", JOptionPane.YES_NO_OPTION);
                    if (i == 0) {
                        for (int x = 0; x < d.getRowCount(); x++) {
                            ResultSet rs = JDBC.getdata("SELECT * FROM stock WHERE iid='" + jTable3.getValueAt(x, 0) + "'");
                            if (rs.next()) {
                                JDBC.putdata("UPDATE stock SET "
                                        + "qty=qty+'" + jTable3.getValueAt(x, 3) + "',"
                                        + "unit_price='" + jTable3.getValueAt(x, 2) + "'"
                                        + "WHERE iid='" + jTable3.getValueAt(x, 0) + "'");
                            } else {
                                JDBC.putdata("INSERT INTO stock VALUES("
                                        + "0,"
                                        + "'" + jTable3.getValueAt(x, 0) + "',"
                                        + "'" + jTable3.getValueAt(x, 1) + "',"
                                        + "'" + jTable3.getValueAt(x, 2) + "',"
                                        + "'" + jTable3.getValueAt(x, 3) + "',"
                                        + "'" + jTable3.getValueAt(x, 0) + "-" + jTable3.getValueAt(x, 1) + "')");
                            }
                        }

                        for (int x = 0; x < d.getRowCount(); x++) {
                            JDBC.putdata("INSERT INTO grn VALUES("
                                    + "'" + jTinvoice_no.getText() + "',"
                                    + "'" + jTable3.getValueAt(x, 0) + "',"
                                    + "'" + jTable3.getValueAt(x, 3) + "',"
                                    + "'" + jTable3.getValueAt(x, 4) + "')");
                        }

                        String sid = null;
                        ResultSet rs = JDBC.getdata("SELECT * FROM supplier WHERE supplier_combo='" + jCGRN_supplier.getSelectedItem() + "'");
                        while (rs.next()) {
                            sid = rs.getString("sid");
                        }

                        JDBC.putdata("INSERT INTO grn_info VALUES("
                                + "'" + jTinvoice_no.getText() + "',"
                                + "'" + QuickDateTime.date() + "',"
                                + "'" + jTItem_count.getText() + "',"
                                + "'" + jTNet_amount.getText() + "',"
                                + "'" + sid + "')");

//                        String cash_account = "DSKH15000001";
//                        String stock_account = "DSKT15000001";

                        double na = Double.parseDouble(jTNet_amount.getText().trim());
//                        double ca = Double.parseDouble(jTCash_amount.getText().trim());
//                        double cha = Double.parseDouble(jTCheque_amount.getText().trim());
//                        double ba = Double.parseDouble(jTBalance.getText().trim());
//
//                        if (jCCash.isSelected() && !(jCCheque.isSelected()) && !(jCCredit.isSelected())) {
//                            //only cash
//                            Account.debit(stock_account, jTinvoice_no.getText(), na, QuickDateTime.date());
//                            Account.credit(cash_account, jTinvoice_no.getText(), na, QuickDateTime.date());
//
//                            Account.supplierPayments(jTinvoice_no.getText(), na, jCCash.getText());
//
//                        } else if (jCCheque.isSelected() && !(jCCash.isSelected()) && !(jCCredit.isSelected())) {
//                            //only cheque
//                            Account.debit(stock_account, jTinvoice_no.getText(), na, QuickDateTime.date());
//                            Account.credit(bank, jTinvoice_no.getText(), cha, QuickDateTime.date());
//
//                            Account.supplierPayments(jTinvoice_no.getText(), cha, jCCheque.getText());
//
//                        } else if (jCCredit.isSelected() && !(jCCheque.isSelected()) && !(jCCash.isSelected())) {
//                            //only credit
//                            Account.debit(stock_account, jTinvoice_no.getText(), na, QuickDateTime.date());
//                            Account.credit(sid, jTinvoice_no.getText(), na, QuickDateTime.date());
//
//                            Account.supplierPayments(jTinvoice_no.getText(), 0, jCCash.getText());
//                        } else if (jCCash.isSelected() && jCCredit.isSelected() && !(jCCheque.isSelected())) {
//                            //cash
//                            Account.debit(stock_account, jTinvoice_no.getText(), ca, QuickDateTime.date());
//                            Account.credit(cash_account, jTinvoice_no.getText(), ca, QuickDateTime.date());
//
//                            Account.supplierPayments(jTinvoice_no.getText(), ca, jCCash.getText());
//                            //credit
//                            Account.debit(stock_account, jTinvoice_no.getText(), ba, QuickDateTime.date());
//                            Account.credit(sid, jTinvoice_no.getText(), ba, QuickDateTime.date());
//
//                        } else if (jCCash.isSelected() && jCCheque.isSelected() && !(jCCredit.isSelected())) {
//                            //cash
//                            Account.debit(stock_account, jTinvoice_no.getText(), (na - cha - ba), QuickDateTime.date());
//                            Account.credit(cash_account, jTinvoice_no.getText(), (na - cha - ba), QuickDateTime.date());
//
//                            Account.supplierPayments(jTinvoice_no.getText(), (na - cha - ba), jCCash.getText());
//                            //cheque
//                            Account.debit(stock_account, jTinvoice_no.getText(), cha, QuickDateTime.date());
//                            Account.credit(bank, jTinvoice_no.getText(), cha, QuickDateTime.date());
//
//                            Account.supplierPayments(jTinvoice_no.getText(), cha, jCCheque.getText());
//                        } else if (jCCredit.isSelected() && jCCheque.isSelected() && !(jCCash.isSelected())) {
//                            //cheque
//                            Account.debit(stock_account, jTinvoice_no.getText(), cha, QuickDateTime.date());
//                            Account.credit(bank, jTinvoice_no.getText(), cha, QuickDateTime.date());
//
//                            Account.supplierPayments(jTinvoice_no.getText(), cha, jCCheque.getText());
//                            //credit
//                            Account.debit(stock_account, jTinvoice_no.getText(), ba, QuickDateTime.date());
//                            Account.credit(sid, jTinvoice_no.getText(), ba, QuickDateTime.date());
//                        } else if (jCCredit.isSelected() && jCCheque.isSelected() && jCCash.isSelected()) {
//                            //cash
//                            Account.debit(stock_account, jTinvoice_no.getText(), ca, QuickDateTime.date());
//                            Account.credit(cash_account, jTinvoice_no.getText(), ca, QuickDateTime.date());
//
//                            Account.supplierPayments(jTinvoice_no.getText(), ca, jCCash.getText());
//                            //cheque
//                            Account.debit(stock_account, jTinvoice_no.getText(), cha, QuickDateTime.date());
//                            Account.credit(bank, jTinvoice_no.getText(), cha, QuickDateTime.date());
//
//                            Account.supplierPayments(jTinvoice_no.getText(), cha, jCCheque.getText());
//                            //credit
//                            Account.debit(stock_account, jTinvoice_no.getText(), ba, QuickDateTime.date());
//                            Account.credit(sid, jTinvoice_no.getText(), ba, QuickDateTime.date());
//                        }

                        d.setRowCount(0);
                        jTItem_count.setText("00.00");
                        jTNet_amount.setText("00.00");
                        jTinvoice_no.setText("");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        NewInvoice1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jButton17 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jLabel34 = new javax.swing.JLabel();
        jTItem_count = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jTNet_amount = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jTGRN_qty = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jCGRN_item = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        jTGRN_up = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTGRN_net = new javax.swing.JTextField();
        jButton7 = new javax.swing.JButton();
        jTinvoice_no1 = new javax.swing.JTextField();
        jPanel30 = new javax.swing.JPanel();
        jLabel67 = new javax.swing.JLabel();
        jTinvoice_no = new javax.swing.JTextField();
        jLabel69 = new javax.swing.JLabel();
        jCGRN_supplier = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jButton23 = new javax.swing.JButton();

        setClosable(true);
        setTitle("Update Stock");

        NewInvoice1.setBackground(new java.awt.Color(255, 255, 255));
        NewInvoice1.setMaximumSize(new java.awt.Dimension(1364, 655));
        NewInvoice1.setMinimumSize(new java.awt.Dimension(1364, 655));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setMaximumSize(new java.awt.Dimension(1350, 212));
        jPanel6.setMinimumSize(new java.awt.Dimension(1350, 212));
        jPanel6.setOpaque(false);

        jButton17.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton17.setText("DELETE");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item No", "Name", "Unit Price", "Qty", "Net Amount(Rs)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, true, true, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTable3);

        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel34.setText("Item Count");

        jTItem_count.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jTItem_count.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTItem_count.setText("00.00");
        jTItem_count.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTItem_countFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTItem_countFocusLost(evt);
            }
        });
        jTItem_count.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTItem_countKeyTyped(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel19.setText("Net Amount(Rs)");

        jTNet_amount.setEditable(false);
        jTNet_amount.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jTNet_amount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTNet_amount.setText("00.00");
        jTNet_amount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTNet_amountActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTItem_count, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTNet_amount, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1069, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTItem_count, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton17)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTNet_amount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setMaximumSize(new java.awt.Dimension(1350, 48));
        jPanel2.setMinimumSize(new java.awt.Dimension(1350, 48));
        jPanel2.setOpaque(false);

        jTGRN_qty.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTGRN_qty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTGRN_qty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTGRN_qtyFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTGRN_qtyFocusLost(evt);
            }
        });
        jTGRN_qty.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTGRN_qtyKeyTyped(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setText("Qty");

        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel33.setText("Item");

        jCGRN_item.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jCGRN_item.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jCGRN_item.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCGRN_itemMouseClicked(evt);
            }
        });
        jCGRN_item.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCGRN_itemActionPerformed(evt);
            }
        });
        jCGRN_item.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jCGRN_itemKeyReleased(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setText("Unit Price(Rs)");

        jTGRN_up.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTGRN_up.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTGRN_up.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTGRN_upFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTGRN_upFocusLost(evt);
            }
        });
        jTGRN_up.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTGRN_upKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTGRN_upKeyTyped(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setText("Net Amount (Rs)");

        jTGRN_net.setEditable(false);
        jTGRN_net.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTGRN_net.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTGRN_netFocusLost(evt);
            }
        });

        jButton7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton7.setText("Add");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jTinvoice_no1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTinvoice_no1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTinvoice_no1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTinvoice_no1KeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel33)
                .addGap(42, 42, 42)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jTinvoice_no1, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTGRN_up, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTGRN_qty, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTGRN_net, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jCGRN_item, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTGRN_up, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTGRN_qty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTGRN_net, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTinvoice_no1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jCGRN_item, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel30.setBackground(new java.awt.Color(255, 255, 255));
        jPanel30.setMaximumSize(new java.awt.Dimension(280, 129));
        jPanel30.setMinimumSize(new java.awt.Dimension(280, 129));
        jPanel30.setName(""); // NOI18N
        jPanel30.setOpaque(false);

        jLabel67.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel67.setText("Invoice No");

        jTinvoice_no.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        jLabel69.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel69.setText("Supplier ");

        jCGRN_supplier.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jCGRN_supplier.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jCGRN_supplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCGRN_supplierActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel67)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTinvoice_no, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel69, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCGRN_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(261, Short.MAX_VALUE))
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel69)
                    .addComponent(jTinvoice_no, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel67)
                    .addComponent(jCGRN_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton23.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton23.setText("Save");
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout NewInvoice1Layout = new javax.swing.GroupLayout(NewInvoice1);
        NewInvoice1.setLayout(NewInvoice1Layout);
        NewInvoice1Layout.setHorizontalGroup(
            NewInvoice1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NewInvoice1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(NewInvoice1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(NewInvoice1Layout.createSequentialGroup()
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 1083, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, NewInvoice1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton23, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22))
                    .addGroup(NewInvoice1Layout.createSequentialGroup()
                        .addGroup(NewInvoice1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(NewInvoice1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1091, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1091, Short.MAX_VALUE))
                            .addGroup(NewInvoice1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jPanel30, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        NewInvoice1Layout.setVerticalGroup(
            NewInvoice1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NewInvoice1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton23)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(NewInvoice1, javax.swing.GroupLayout.PREFERRED_SIZE, 1101, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(NewInvoice1, javax.swing.GroupLayout.PREFERRED_SIZE, 505, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        try {
            int ii = Integer.parseInt(jTable3.getValueAt(jTable3.getSelectedRow(), 3).toString().trim());
            DefaultTableModel d = (DefaultTableModel) jTable3.getModel();
            if (d.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No data entered", "Input error", JOptionPane.ERROR_MESSAGE);
            } else {
                int i = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected item?", "", JOptionPane.YES_NO_OPTION);
                if (i == 0) {
                    d.removeRow(jTable3.getSelectedRow());
                    GRN_item_count();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No item selected", "Input error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jTGRN_qtyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTGRN_qtyFocusGained
        try {
            jTGRN_net.setText("");
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jTGRN_qtyFocusGained

    private void jTGRN_qtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTGRN_qtyFocusLost
        try {
            double up = Double.parseDouble(jTGRN_up.getText());
            double qty = Double.parseDouble(jTGRN_qty.getText());
            jTGRN_net.setText(df.format(up * qty));
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jTGRN_qtyFocusLost

    private void jTGRN_qtyKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTGRN_qtyKeyTyped
        char c = evt.getKeyChar();
        if ((Character.isDigit(c)) || (c == '.')) {
        } else {
            evt.consume();
        }
        String s = jTGRN_qty.getText();
        if (s.length() >= 10) {
            evt.consume();
        }
    }//GEN-LAST:event_jTGRN_qtyKeyTyped

    private void jCGRN_itemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCGRN_itemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCGRN_itemActionPerformed

    private void jTGRN_upFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTGRN_upFocusGained
        try {
            jTGRN_net.setText("");
            jTGRN_qty.setText("");
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jTGRN_upFocusGained

    private void jTGRN_upFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTGRN_upFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_jTGRN_upFocusLost

    private void jTGRN_upKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTGRN_upKeyReleased
        //
    }//GEN-LAST:event_jTGRN_upKeyReleased

    private void jTGRN_upKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTGRN_upKeyTyped
        char c = evt.getKeyChar();
        if ((Character.isDigit(c)) || (c == '.')) {
        } else {
            evt.consume();
        }
        String s = jTGRN_up.getText();
        if (s.length() >= 10) {
            evt.consume();
        }
    }//GEN-LAST:event_jTGRN_upKeyTyped

    private void jTGRN_netFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTGRN_netFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_jTGRN_netFocusLost

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        try {
            if (jTGRN_up.getText().isEmpty() || jTGRN_qty.getText().isEmpty() || jTGRN_net.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Some fields are empty", "Input error", JOptionPane.ERROR_MESSAGE);
            } else {
                ResultSet rs_check = JDBC.getdata("SELECT * FROM items WHERE name='" + jTinvoice_no1.getText().toString().trim() + "'");
                if (rs_check.next()) {
                    ResultSet rs = JDBC.getdata("SELECT * FROM items WHERE name='" + jTinvoice_no1.getText().toString().trim() + "'");
                    String iid = "";
                    while (rs.next()) {
                        iid = rs.getString("iid");
                    }
                    if (checkDuplicate(0, jTable3, iid)) {
                        JOptionPane.showMessageDialog(null, "This item is already entered", "Input error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        addTable();
                        GRN_item_count();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No such item registered", "Input error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jCGRN_supplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCGRN_supplierActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCGRN_supplierActionPerformed

    private void jTNet_amountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTNet_amountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTNet_amountActionPerformed

    private void jTItem_countFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTItem_countFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_jTItem_countFocusGained

    private void jTItem_countFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTItem_countFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_jTItem_countFocusLost

    private void jTItem_countKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTItem_countKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_jTItem_countKeyTyped

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        try {
//            if (jCCheque.isSelected()) {
//                if (jTinvoice_no.getText().isEmpty()) {
//                    JOptionPane.showMessageDialog(null, "Please enter invoice number", "Input error", JOptionPane.ERROR_MESSAGE);
//                } else if (jCGRN_supplier.getSelectedItem().equals("")) {
//                    JOptionPane.showMessageDialog(null, "Please select supplier", "Input error", JOptionPane.ERROR_MESSAGE);
//                } else if (!(jCCash.isSelected() || jCCheque.isSelected() || jCCredit.isSelected())) {
//                    JOptionPane.showMessageDialog(null, "Please select payment method", "Input error", JOptionPane.ERROR_MESSAGE);
//                } else if (jCCash.isSelected() && jTCash_amount.getText().equals("00.00")) {
//                    JOptionPane.showMessageDialog(null, "Please enter cash amount", "Input error", JOptionPane.ERROR_MESSAGE);
//                } else if (jCCheque.isSelected() && jTCheque_amount.getText().equals("00.00")) {
//                    JOptionPane.showMessageDialog(null, "Please enter cheque amount", "Input error", JOptionPane.ERROR_MESSAGE);
//                } else if (cal()) {
//                    //
//                } else {
//                    double cha = Double.parseDouble(jTCheque_amount.getText().trim());
//                    ChequeDetailsUpdateStock cdus = new ChequeDetailsUpdateStock();
//                    cdus.jTAmount.setText(df.format(cha));
//                    Dimension desktopSize = jDesktopPane1.getSize();
//                    Dimension jInternalFrameSize = cdus.getSize();
//                    cdus.setLocation((desktopSize.width - jInternalFrameSize.width) / 2, (desktopSize.height - jInternalFrameSize.height) / 2);
//                    Home.jDesktopPane1.add(cdus).setVisible(true);
//                }
//            } else {
            toDB();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton23ActionPerformed

    private void jCGRN_itemKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCGRN_itemKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTinvoice_no1.setText(jCGRN_item.getSelectedItem().toString());
        }
    }//GEN-LAST:event_jCGRN_itemKeyReleased

    private void jTinvoice_no1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTinvoice_no1KeyReleased
        combo();
    }//GEN-LAST:event_jTinvoice_no1KeyReleased

    private void jTinvoice_no1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTinvoice_no1KeyPressed
        if (evt.getKeyCode() == evt.VK_DOWN) {
            jCGRN_item.grabFocus();
        }
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTinvoice_no1.setText(jCGRN_item.getSelectedItem().toString());
        }
    }//GEN-LAST:event_jTinvoice_no1KeyPressed

    private void jCGRN_itemMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCGRN_itemMouseClicked
        jTinvoice_no1.setText(jCGRN_item.getSelectedItem().toString());
    }//GEN-LAST:event_jCGRN_itemMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel NewInvoice1;
    public static javax.swing.JButton jButton17;
    public static javax.swing.JButton jButton23;
    public static javax.swing.JButton jButton7;
    public static javax.swing.JComboBox jCGRN_item;
    public static javax.swing.JComboBox jCGRN_supplier;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator4;
    public static javax.swing.JTextField jTGRN_net;
    public static javax.swing.JTextField jTGRN_qty;
    public static javax.swing.JTextField jTGRN_up;
    public static javax.swing.JTextField jTItem_count;
    public static javax.swing.JTextField jTNet_amount;
    public static javax.swing.JTable jTable3;
    public static javax.swing.JTextField jTinvoice_no;
    public static javax.swing.JTextField jTinvoice_no1;
    // End of variables declaration//GEN-END:variables
}
