/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rsa;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigInteger;
import java.util.StringTokenizer;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Form extends javax.swing.JFrame {

    // declaring Variables
    private RSA rsa;
    private RSA cypherRSA;
    private Enc_Window app;

    /**
     * end of Variables declaration
     * 
     * init Constructor
     */
    public Form() {
        initComponents();

        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Don't run usual closing action

        this.addWindowListener(new WindowAdapter() {
            /**
             * Detects close request, confirms if user really wants to close if
             * textBox is empty, server has to be closed, so window notifies
             * user that he has to close Server window
             *
             * @param ev
             */
            public void windowClosing(WindowEvent ev) {
                if (JOptionPane.showConfirmDialog(null, "Close this Window?", "WARNING", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    hideWindow();
                    if (txtMessage.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Empty Box, close Server", "WARNING", JOptionPane.INFORMATION_MESSAGE);
                    }
                    System.exit(0);
                }
            }
        });

        this.txtD.setEditable(false);
        this.txtE.setEditable(false);
        this.txtN.setEditable(false);
        this.txtPhi.setEditable(false);

        this.txtD.setRequestFocusEnabled(false);
        this.txtE.setRequestFocusEnabled(false);
        this.txtN.setRequestFocusEnabled(false);
        this.txtPhi.setRequestFocusEnabled(false);
    }

    /**
     * end of Constructor
     * 
     * init methods
     */
    private void hideWindow() {
        this.setVisible(false);
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
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        P_JLabel = new javax.swing.JLabel();
        Q_JLabel = new javax.swing.JLabel();
        txtP = new javax.swing.JTextField();
        txtQ = new javax.swing.JTextField();
        buttonGenerate = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        N_JLabel = new javax.swing.JLabel();
        txtN = new javax.swing.JTextField();
        Phi_JLabel = new javax.swing.JLabel();
        txtPhi = new javax.swing.JTextField();
        E_JLabel = new javax.swing.JLabel();
        txtE = new javax.swing.JTextField();
        D_JLabel = new javax.swing.JLabel();
        txtD = new javax.swing.JTextField();
        buttonCalculate = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        ButtonCypher = new javax.swing.JRadioButton();
        ButtonDesc = new javax.swing.JRadioButton();
        txtMessage = new javax.swing.JTextField();
        Message_JLabel = new javax.swing.JLabel();
        ButtonOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Message Encryption", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Brush Script Std", 0, 24), new java.awt.Color(51, 153, 255))); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "RSA Parameters", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Brush Script Std", 0, 18), new java.awt.Color(51, 153, 255))); // NOI18N

        P_JLabel.setText("P Number");

        Q_JLabel.setText("Q Number");

        buttonGenerate.setText("Generate");
        buttonGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGenerateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(P_JLabel)
                    .addComponent(Q_JLabel))
                .addGap(79, 79, 79)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtQ, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtP, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonGenerate)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(P_JLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Q_JLabel)
                            .addComponent(txtQ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(buttonGenerate)))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Keys", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Brush Script Std", 0, 18), new java.awt.Color(51, 153, 255))); // NOI18N

        N_JLabel.setText("N Mode");

        Phi_JLabel.setText("Phi(n) = (p-1)(q-1) ");

        E_JLabel.setText("Public Key (e)");

        D_JLabel.setText("Private Key (d)");

        buttonCalculate.setText("Calculate");
        buttonCalculate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCalculateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(Phi_JLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(E_JLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(D_JLabel)
                    .addComponent(N_JLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(35, 35, 35)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtN, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPhi)
                            .addComponent(txtE, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtD, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonCalculate)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(N_JLabel)
                    .addComponent(txtN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Phi_JLabel)
                            .addComponent(txtPhi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(E_JLabel)
                            .addComponent(txtE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(buttonCalculate)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(D_JLabel)
                    .addComponent(txtD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Encryption", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Brush Script Std", 0, 18), new java.awt.Color(51, 153, 255))); // NOI18N

        buttonGroup1.add(ButtonCypher);
        ButtonCypher.setText("Cypher");
        ButtonCypher.setEnabled(false);

        buttonGroup1.add(ButtonDesc);
        ButtonDesc.setText("Descypher");
        ButtonDesc.setEnabled(false);

        txtMessage.setEditable(false);
        txtMessage.setScrollOffset(15);

        Message_JLabel.setText("Message:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ButtonDesc)
                    .addComponent(ButtonCypher))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Message_JLabel)
                    .addComponent(txtMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ButtonCypher)
                    .addComponent(Message_JLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ButtonDesc)
                    .addComponent(txtMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        ButtonOk.setText("Ok");
        ButtonOk.setEnabled(false);
        ButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonOkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(170, 170, 170)
                .addComponent(ButtonOk)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ButtonOk)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGenerateActionPerformed
        this.txtP.setEditable(false);
        this.txtQ.setEditable(false);

        this.txtP.setRequestFocusEnabled(false);
        this.txtQ.setRequestFocusEnabled(false);

        rsa = new RSA(10, app);

        /**
         * @note: Create a RSA() instance to set txtP and txtQ with the values
         * calculated in getParameters() process
         */
        this.txtP.setText("" + rsa.getNump());
        this.txtQ.setText("" + rsa.getNumq());
    }//GEN-LAST:event_buttonGenerateActionPerformed

    private void buttonCalculateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCalculateActionPerformed
        if (this.txtP.getText().isEmpty() || this.txtQ.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Write Parameters or Press Generate Button", "ERROR", JOptionPane.ERROR_MESSAGE);
        } else {
            BigInteger p = new BigInteger(this.txtP.getText());
            BigInteger q = new BigInteger(this.txtQ.getText());

            cypherRSA = new RSA(10, p, q, app);

            /**
             * @note:
             *
             * creates the RSA() new instance that will be the same which runs
             * the encrypted() and decrypted() methods
             */
            this.txtE.setText("" + cypherRSA.getNume());
            this.txtD.setText("" + cypherRSA.getNumd());
            this.txtN.setText("" + cypherRSA.getNumn());
            this.txtPhi.setText("" + cypherRSA.getNumPhi());
            this.txtMessage.setText("");

            this.ButtonCypher.setEnabled(true);
            this.ButtonDesc.setEnabled(true);
            this.txtMessage.setEditable(true);
            this.ButtonOk.setEnabled(true);
        }
    }//GEN-LAST:event_buttonCalculateActionPerformed

    private void ButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonOkActionPerformed
        if (this.txtMessage.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Empty Message", "ERROR", JOptionPane.ERROR_MESSAGE);
        } else if (!this.ButtonCypher.isSelected() && !this.ButtonDesc.isSelected()) {
            JOptionPane.showMessageDialog(this, "Click on Cypher or Descypher", "ERROR", JOptionPane.ERROR_MESSAGE);
        } else {
            if (this.ButtonCypher.isSelected()) {
                BigInteger[] cypherText = cypherRSA.Encrypted(this.txtMessage.getText());
                this.txtMessage.setText("");
                for (int i = 0; i < cypherText.length; i++) {
                    this.txtMessage.setText(this.txtMessage.getText() + cypherText[i].toString() + "\t");

                    if (i != cypherText.length - 1) {
                        this.txtMessage.setText(this.txtMessage.getText() + "");
                    }
                }
                app.sendData(cypherText);
            } else {
                String letter = "";
                StringTokenizer st = new StringTokenizer(this.txtMessage.getText());
                BigInteger[] cypherText = new BigInteger[st.countTokens()];

                for (int i = 0; i < cypherText.length; i++) {
                    letter = st.nextToken();
                    cypherText[i] = new BigInteger(letter);
                }

                this.txtMessage.setText(cypherRSA.Decrypted(cypherText));
            }
        }

    }//GEN-LAST:event_ButtonOkActionPerformed

    public void setApp(Enc_Window app) {
        this.app = app;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton ButtonCypher;
    private javax.swing.JRadioButton ButtonDesc;
    private javax.swing.JButton ButtonOk;
    private javax.swing.JLabel D_JLabel;
    private javax.swing.JLabel E_JLabel;
    private javax.swing.JLabel Message_JLabel;
    private javax.swing.JLabel N_JLabel;
    private javax.swing.JLabel P_JLabel;
    private javax.swing.JLabel Phi_JLabel;
    private javax.swing.JLabel Q_JLabel;
    private javax.swing.JButton buttonCalculate;
    private javax.swing.JButton buttonGenerate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField txtD;
    private javax.swing.JTextField txtE;
    private javax.swing.JTextField txtMessage;
    private javax.swing.JTextField txtN;
    private javax.swing.JTextField txtP;
    private javax.swing.JTextField txtPhi;
    private javax.swing.JTextField txtQ;
    // End of variables declaration//GEN-END:variables
}
