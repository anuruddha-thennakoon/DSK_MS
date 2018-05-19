/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author ANURUDDHA
 */
public class Invoice extends javax.swing.JInternalFrame {

    static DecimalFormat df = new DecimalFormat("00.00");
    static DecimalFormat dfwd = new DecimalFormat("00");

    /**
     * Creates new form Invoice
     */
    public Invoice() {
        initComponents();
        invoiceNo();
        itemCombo();
        route();

    }

    void combo() {
        try {
            ResultSet rs = JDBC.getdata("SELECT * FROM stock WHERE name LIKE '" + jTitem.getText() + "%' AND qty>0");
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

    void route() {
        try {
            ResultSet rs = JDBC.getdata("SELECT * FROM route");
            Vector v = new Vector();
            while (rs.next()) {
                v.add(rs.getString("route_cc"));
            }
            jComboBox1.setModel(new DefaultComboBoxModel(v));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void customer() {
        try {
            ResultSet rs = JDBC.getdata("SELECT * FROM customer WHERE route='" + jComboBox1.getSelectedItem().toString().trim() + "' AND status='Active'");
            Vector v = new Vector();
            while (rs.next()) {
                v.add(rs.getString("customer_combo"));
            }
            jCInvo_customer.setModel(new DefaultComboBoxModel(v));
        } catch (Exception e) {
        }
    }

    public static void invoiceNo() {
        try {
            ResultSet rs = JDBC.getdata("select MAX(invoice_no) FROM invoice_info");
            while (rs.next()) {
                String batch1 = rs.getString("max(invoice_no)");
                int batch2 = Integer.parseInt(batch1.substring(4));
                int batch3 = (batch2 + 1);
                jTinvoice_no.setText("DSKI" + batch3);
            }
        } catch (Exception e) {
            jTinvoice_no.setText("DSKI" + 1001);
        }
    }

    void itemCombo() {
        try {
            ResultSet rs = JDBC.getdata("SELECT DISTINCT stock_combo FROM stock WHERE qty>0");
            Vector v = new Vector();
            while (rs.next()) {
                v.add(rs.getString("stock_combo"));
            }
            jCGRN_item.setModel(new DefaultComboBoxModel(v));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void invoiceTable() {
        String iid = null;
        String iname = null;
        double cost = 0;
        try {
            ResultSet rs = JDBC.getdata("SELECT * FROM items WHERE name='" + jTitem.getText().toString().trim() + "'");
            while (rs.next()) {
                iid = rs.getString("iid");
                iname = rs.getString("name");
            }

            ResultSet rs_cost = JDBC.getdata("SELECT * FROM stock WHERE name='" + jTitem.getText().toString().trim() + "'");
            while (rs_cost.next()) {
                cost = Double.parseDouble(rs_cost.getString("unit_price"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        double qty = Double.parseDouble(jTextField3.getText().trim().toString());
        double price = Double.parseDouble(jTUnitPrice.getText().trim().toString());
        DefaultTableModel d = (DefaultTableModel) jTable3.getModel();
        Vector v = new Vector();
        v.add(iid);
        v.add(iname);
        v.add(jTUnitPrice.getText().trim());
        v.add(dfwd.format(Double.parseDouble(jTextField3.getText().trim())));
        v.add(df.format(qty * price));
        v.add(df.format(qty * cost));
        d.addRow(v);
        jTitem.setText("");
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

    void toatalAmount() {
        try {
            DefaultTableModel d = (DefaultTableModel) jTable3.getModel();

            double sub_total = 0;
            double cost = 0;

            for (int x = 0; x < d.getRowCount(); x++) {
                sub_total = sub_total + Double.parseDouble(jTable3.getValueAt(x, 4).toString());
                cost = cost + Double.parseDouble(jTable3.getValueAt(x, 5).toString());
            }

            jTItem_count.setText(dfwd.format(d.getRowCount()));
            jTCost.setText(df.format(cost));
            jTNet_amount.setText(df.format(sub_total));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void toDb() {
        try {
            DefaultTableModel d = (DefaultTableModel) jTable3.getModel();
            if (d.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No items added to save", "Input error", JOptionPane.ERROR_MESSAGE);
            } else {
                if (jTinvoice_no.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter invoice number", "Input error", JOptionPane.ERROR_MESSAGE);
                } else if (jCInvo_customer.getSelectedItem().equals(null)) {
                    JOptionPane.showMessageDialog(null, "Please select customer", "Input error", JOptionPane.ERROR_MESSAGE);
                } else {
                    int i = JOptionPane.showConfirmDialog(null, "Are you sure you want to save?", "", JOptionPane.YES_NO_OPTION);
                    if (i == 0) {
                        if (printInvoice()) {
                            for (int x = 0; x < d.getRowCount(); x++) {
                                JDBC.putdata("UPDATE stock SET "
                                        + "qty=qty-'" + jTable3.getValueAt(x, 3) + "' "
                                        + "WHERE iid='" + jTable3.getValueAt(x, 0) + "'");
                            }

                            String cid = jCInvo_customer.getSelectedItem().toString().trim();

                            //invoiccce info
                            JDBC.putdata("INSERT INTO invoice_info VALUES("
                                    + "'" + jTinvoice_no.getText() + "',"
                                    + "'" + QuickDateTime.date() + "',"
                                    + "'" + jTItem_count.getText() + "',"
                                    + "'" + jTNet_amount.getText() + "',"
                                    + "'" + cid + "',"
                                    + "'" + jComboBox1.getSelectedItem().toString() + "',"
                                    + "'" + jTCost.getText() + "')");

                            //invoice
                            for (int x = 0; x < d.getRowCount(); x++) {
                                JDBC.putdata("INSERT INTO invoice VALUES("
                                        + "'" + jTinvoice_no.getText() + "',"
                                        + "'" + jTable3.getValueAt(x, 0) + "',"
                                        + "'" + jTable3.getValueAt(x, 1) + "',"
                                        + "'" + jTable3.getValueAt(x, 2) + "',"
                                        + "'" + jTable3.getValueAt(x, 3) + "',"
                                        + "'" + jTable3.getValueAt(x, 4) + "',"
                                        + "'" + QuickDateTime.date() + "')");
                            }

                            d.setRowCount(0);
                            jTItem_count.setText("00.00");
                            jTNet_amount.setText("00.00");
                            jTCost.setText("00.00");
                            invoiceNo();
                        } else {
                            JOptionPane.showMessageDialog(null, "There is an error while generating the bill", "System error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null, "Some values are empty", "Input error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e, "Input error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static boolean printInvoice() {
        boolean bool = true;
        try {
            String fname = "";
            String lname = "";
            String name = "";
            String contact_no1 = "";
            String contact_no2 = "";
            ResultSet rs_customer = JDBC.getdata("SELECT * FROM customer WHERE customer_combo='" + jCInvo_customer.getSelectedItem().toString() + "'");
            while (rs_customer.next()) {
                fname = rs_customer.getString("fname");
                lname = rs_customer.getString("address");
                contact_no1 = rs_customer.getString("contact_no1");
                contact_no2 = rs_customer.getString("contact_no1");
            }
            name = fname + " " + lname;
            HashMap para = new HashMap();

            para.put("ino", jTinvoice_no.getText().trim());
            para.put("name", name);
            para.put("contact_no1", contact_no1);
            para.put("contact_no2", contact_no2);
            para.put("net_amount", jTNet_amount.getText().trim());
            para.put("item_count", jTItem_count.getText().trim());

            JasperReport r = JasperCompileManager.compileReport("C:\\reports\\dsk\\invoice.jrxml");

            JasperPrint jp = JasperFillManager.fillReport(r, para, new JRTableModelDataSource(jTable3.getModel()));
            JasperViewer.viewReport(jp, false);
        } catch (Exception e) {
            bool = false;
            e.printStackTrace();
        }
        return bool;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        NewInvoice1 = new javax.swing.JPanel();
        jPanel30 = new javax.swing.JPanel();
        jLabel67 = new javax.swing.JLabel();
        jTinvoice_no = new javax.swing.JTextField();
        jLabel69 = new javax.swing.JLabel();
        jCInvo_customer = new javax.swing.JComboBox();
        jLabel70 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator1 = new javax.swing.JSeparator();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jButton18 = new javax.swing.JButton();
        jTItem_count = new javax.swing.JTextField();
        jCGRN = new javax.swing.JLabel();
        jCGRN_item = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        jTUnitPrice = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton7 = new javax.swing.JButton();
        jTCost = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        jTNet_amount = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jTitem = new javax.swing.JTextField();

        setClosable(true);
        setTitle("Invoice");
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });

        NewInvoice1.setBackground(new java.awt.Color(255, 255, 255));
        NewInvoice1.setMaximumSize(new java.awt.Dimension(1364, 655));
        NewInvoice1.setMinimumSize(new java.awt.Dimension(1364, 655));
        NewInvoice1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NewInvoice1KeyPressed(evt);
            }
        });

        jPanel30.setBackground(new java.awt.Color(255, 255, 255));
        jPanel30.setMaximumSize(new java.awt.Dimension(280, 129));
        jPanel30.setMinimumSize(new java.awt.Dimension(280, 129));
        jPanel30.setName(""); // NOI18N
        jPanel30.setOpaque(false);

        jLabel67.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel67.setText("Invoice");
        jLabel67.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel67MouseClicked(evt);
            }
        });

        jTinvoice_no.setEditable(false);
        jTinvoice_no.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        jLabel69.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel69.setText("Customer");

        jCInvo_customer.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jCInvo_customer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCInvo_customerActionPerformed(evt);
            }
        });

        jLabel70.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel70.setText("Route");

        jComboBox1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addComponent(jLabel67)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTinvoice_no, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel70)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel69)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCInvo_customer, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel67)
                    .addComponent(jTinvoice_no, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel70)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel69)
                    .addComponent(jCInvo_customer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton1.setText("Print");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel34.setText("Item Count");

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item ID", "Name", "Unit price(Rs)", "Qty", "Net amount(Rs)", "Cost(Rs)"
            }
        ));
        jScrollPane4.setViewportView(jTable3);

        jButton18.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton18.setText("DELETE");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jTItem_count.setEditable(false);
        jTItem_count.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
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

        jCGRN.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jCGRN.setText("Item");

        jCGRN_item.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jCGRN_item.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCGRN_itemActionPerformed(evt);
            }
        });
        jCGRN_item.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCGRN_itemKeyPressed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setText("Unit Price(Rs)");

        jTUnitPrice.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTUnitPrice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTUnitPrice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTUnitPriceFocusLost(evt);
            }
        });
        jTUnitPrice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTUnitPriceKeyTyped(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setText("Qty");

        jTextField3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextField3.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField3FocusLost(evt);
            }
        });
        jTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField3KeyTyped(evt);
            }
        });

        jButton7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton7.setText("Add");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jTCost.setEditable(false);
        jTCost.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTCost.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTCost.setText("00.00");
        jTCost.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTCostFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTCostFocusLost(evt);
            }
        });
        jTCost.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTCostKeyTyped(evt);
            }
        });

        jLabel39.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel39.setText("Cost(Rs)");

        jTNet_amount.setEditable(false);
        jTNet_amount.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTNet_amount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTNet_amount.setText("00.00");
        jTNet_amount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTNet_amountActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel18.setText("Net Amount(Rs)");

        jTitem.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTitem.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jTitem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTitemKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTitemKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jCGRN, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jCGRN_item, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTitem, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTUnitPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 341, Short.MAX_VALUE)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane4)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTItem_count, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel39)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTCost, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTNet_amount, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTUnitPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCGRN, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addComponent(jCGRN_item, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jTNet_amount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(jTCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39)
                    .addComponent(jTItem_count, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34)
                    .addComponent(jButton18))
                .addContainerGap())
        );

        javax.swing.GroupLayout NewInvoice1Layout = new javax.swing.GroupLayout(NewInvoice1);
        NewInvoice1.setLayout(NewInvoice1Layout);
        NewInvoice1Layout.setHorizontalGroup(
            NewInvoice1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NewInvoice1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(NewInvoice1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(NewInvoice1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(NewInvoice1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        NewInvoice1Layout.setVerticalGroup(
            NewInvoice1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NewInvoice1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(NewInvoice1, javax.swing.GroupLayout.PREFERRED_SIZE, 1116, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(NewInvoice1, javax.swing.GroupLayout.PREFERRED_SIZE, 522, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTNet_amountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTNet_amountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTNet_amountActionPerformed

    private void jCInvo_customerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCInvo_customerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCInvo_customerActionPerformed

    private void jCGRN_itemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCGRN_itemActionPerformed
        //
    }//GEN-LAST:event_jCGRN_itemActionPerformed

    private void jTextField3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField3FocusLost
        try {
            double qty = Double.parseDouble(jTextField3.getText().toString().trim());
            ResultSet rs = JDBC.getdata("SELECT * FROM stock WHERE qty>='" + qty + "' AND name='" + jTitem.getText().toString().trim() + "'");
            if (rs.next()) {
            } else {
                JOptionPane.showMessageDialog(null, "Not enough qty", "Input error", JOptionPane.ERROR_MESSAGE);
                jTextField3.setText("");
                jTextField3.requestFocus();
            }
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jTextField3FocusLost

    private void jTextField3KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyTyped
        char c = evt.getKeyChar();
        if ((Character.isDigit(c))) {
        } else {
            evt.consume();
        }
        String s = jTextField3.getText();
        if (s.length() >= 10) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField3KeyTyped

    private void jTUnitPriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTUnitPriceFocusLost
        //        try {
        //            double up = Double.parseDouble(jTextField5.getText().toString().trim());
        //            ResultSet rs = JDBC.getdata("SELECT * FROM stock WHERE unit_price<='" + up + "' AND item_combo='" + jCInvo_item.getSelectedItem() + "'");
        //            if (rs.next()) {
        //            } else {
        //                JOptionPane.showMessageDialog(null, "Unit price is low", "Input error", JOptionPane.ERROR_MESSAGE);
        //                jTextField5.setText("");
        //                jTextField5.requestFocus();
        //            }
        //        } catch (Exception e) {
        //        }
    }//GEN-LAST:event_jTUnitPriceFocusLost

    private void jTUnitPriceKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTUnitPriceKeyTyped
        try {
            char c = evt.getKeyChar();
            if ((Character.isDigit(c)) || (c == '.')) {
            } else {
                evt.consume();
            }
            String s = jTUnitPrice.getText();
            if (s.length() >= 10) {
                evt.consume();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jTUnitPriceKeyTyped

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        try {
            if (jTUnitPrice.getText().isEmpty() || jTextField3.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Some fields are empty", "Input error", JOptionPane.ERROR_MESSAGE);
            } else {
                ResultSet rs_check = JDBC.getdata("SELECT * FROM items WHERE name='" + jTitem.getText().toString().trim() + "'");
                if (rs_check.next()) {
                    ResultSet rs = JDBC.getdata("SELECT * FROM items WHERE name='" + jTitem.getText().toString().trim() + "'");
                    String iid = "";
                    while (rs.next()) {
                        iid = rs.getString("iid");
                    }
                    if (checkDuplicate(0, jTable3, iid)) {
                        JOptionPane.showMessageDialog(null, "This item is already entered", "Input error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        invoiceTable();
                        toatalAmount();
                        jTitem.requestFocus();
                        jTUnitPrice.setText("");
                        jTextField3.setText("");
                        jTitem.setText("");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No such item found in stock", "Input error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        try {
//            int ii = Integer.parseInt(jTable3.getValueAt(jTable3.getSelectedRow(), 3).toString().trim());
            DefaultTableModel d = (DefaultTableModel) jTable3.getModel();
            if (d.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No data entered", "Input error", JOptionPane.ERROR_MESSAGE);
            } else {
                int i = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected item?", "", JOptionPane.YES_NO_OPTION);
                if (i == 0) {
                    d.removeRow(jTable3.getSelectedRow());
                    toatalAmount();
                }
            }
        } catch (Exception e) {
//            JOptionPane.showMessageDialog(null, "No item selected", "Input error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jTItem_countFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTItem_countFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_jTItem_countFocusGained

    private void jTItem_countFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTItem_countFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_jTItem_countFocusLost

    private void jTItem_countKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTItem_countKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_jTItem_countKeyTyped

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        customer();
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            toDb();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTCostFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTCostFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_jTCostFocusGained

    private void jTCostFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTCostFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_jTCostFocusLost

    private void jTCostKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTCostKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_jTCostKeyTyped

    private void jCGRN_itemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCGRN_itemKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTitem.setText(jCGRN_item.getSelectedItem().toString());
        }
    }//GEN-LAST:event_jCGRN_itemKeyPressed

    private void jTitemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTitemKeyPressed
        if (evt.getKeyCode() == evt.VK_DOWN) {
            jCGRN_item.grabFocus();
        }
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTitem.setText(jCGRN_item.getSelectedItem().toString());
            jTUnitPrice.requestFocus();
        }
    }//GEN-LAST:event_jTitemKeyPressed

    private void jTitemKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTitemKeyReleased
        combo();
    }//GEN-LAST:event_jTitemKeyReleased

    private void NewInvoice1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NewInvoice1KeyPressed
//        
    }//GEN-LAST:event_NewInvoice1KeyPressed

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        jTitem.requestFocus();
    }//GEN-LAST:event_formFocusGained

    private void jLabel67MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel67MouseClicked
        if (jTinvoice_no.isEditable()) {
            jTinvoice_no.setEditable(false);
        } else {
            jTinvoice_no.setEditable(true);
        }
    }//GEN-LAST:event_jLabel67MouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel NewInvoice1;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    public static javax.swing.JButton jButton18;
    public static javax.swing.JButton jButton7;
    private javax.swing.JLabel jCGRN;
    public static javax.swing.JComboBox jCGRN_item;
    public static javax.swing.JComboBox jCInvo_customer;
    public static javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    public static javax.swing.JTextField jTCost;
    public static javax.swing.JTextField jTItem_count;
    public static javax.swing.JTextField jTNet_amount;
    public static javax.swing.JTextField jTUnitPrice;
    public static javax.swing.JTable jTable3;
    public static javax.swing.JTextField jTextField3;
    public static javax.swing.JTextField jTinvoice_no;
    private javax.swing.JTextField jTitem;
    // End of variables declaration//GEN-END:variables
}
