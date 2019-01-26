package server;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class Server extends JFrame {

    private JTextField textField;
    private JTextArea screenArea;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket socketClient;
    private int cont = 1;

    private BigInteger numE, numN;          // Values for connection with RSA Window
    private BigInteger[] encrypted = null;  // To save encrypted message received from RSA
    private BigInteger numS;                // Value written by client

    public Server() {

        super("Server");

        Container container = getContentPane();

        textField = new JTextField();
        textField.setEditable(false);                      
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                sendData(evento.getActionCommand());
                textField.setText("");
            }
        });

        container.add(textField, BorderLayout.NORTH);

        screenArea = new JTextArea();
        container.add((new JScrollPane(screenArea)));
        setSize(300, 150);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        runServer(false);           // Start the Process
    }

    public void runServer( boolean decrypting) {

        try {
            server = new ServerSocket(12345, 100);  // Initializing Server Socket with port 12345

            try {
                wait_f_Connection();                // Define client and wait for answer
                getFlows();                         // Initializing Streams to communicate with client
                processConnection(decrypting);       // After connecting, give success message and start client message claiming
            } catch (EOFException e) {
                JOptionPane.showMessageDialog(this, "Connection Closed", "WARNING", JOptionPane.WARNING_MESSAGE);
            }
        } catch (IOException ex) {
            System.out.println("Logout");

            if (output != null) {
                closeConnection();
                closeServer();
            }
            
            /**
             * @note;
             * Without any change in code,
             * process below should be done just once again
             * to make the connection with client window
             */

            if (JOptionPane.showConfirmDialog(this, "run Server?", "WARNING", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                showMessage("\nTrying to run again");
                restart();                      // Making Logger Variables Null to restart the connection then
                runServer(true);                // Restart the process
            }
            
            cont++;
        }

        if (output != null) {
            closeConnection();
            closeServer();
        }
    }

    private void restart() {
        server = null;
        output = null;
        input = null;
        socketClient = null;
    }

    private void wait_f_Connection() throws IOException {
        showMessage("\nTrying to Connect\n");
        socketClient = server.accept();
        
        /**
         * @note:
         * Program WILL NOT continue 'til
         * a client is released
         */
        
    }

    private void getFlows() throws IOException {
        output = new ObjectOutputStream(socketClient.getOutputStream());
        output.flush();

        input = new ObjectInputStream(socketClient.getInputStream());
        showMessage("\nI/O Flows gotten successfully");
    }

    private void processConnection(boolean decrypting) throws IOException {

        boolean ignore = false;

        String message = "Successfully Connected";
        sendData(message);
        setTextFieldEditable(true);             // To be able to write personal messages
        do {
            try {
                if (!decrypting) {
                    message = (String) input.readObject();
                    /**
                     * Program will wait 'til client
                     * sends a message
                     */
                    if (message.equals("Client >>> FINISH")) {
                        int answer = JOptionPane.showConfirmDialog(this, "Sure you want to finish connection?", "WARNING", JOptionPane.YES_NO_OPTION);
                        if (answer != JOptionPane.YES_OPTION) {
                            message = "Refused LOGOUT";
                            showMessage(message);
                        }
                    } else if (isBigInteger(message)) {
                        
                        /**
                         * @note:
                         * Check if message comes in format
                         * "key: 'number' mode: 'number'
                         */
                        
                        String e = "", n = "";
                        boolean _e = true;
                        // Concatening message digits to get the value
                        for (int i = 5; i < message.length(); i++) {
                            if (message.charAt(i) == ' ') {
                                i += 7;
                                _e = false;
                            }
                            if (_e) {
                                e = e + message.charAt(i); 
                            } else {
                                n = n + message.charAt(i);
                            }
                        }
                        System.out.println("\nPublic Key (" + e + ") mode " + n + " detected");
                        numE = new BigInteger(e);
                        numN = new BigInteger(n);
                    } else if (Character.isDigit(message.charAt(1))) { 
                        
                        /**
                         * @note:
                         * if the first char after '[' is Digit,
                         * then it runs this process
                         * 
                         * temp[0] is modified to substract the '['
                         * in '[...'
                         * 
                         * Also, temp[temp.length-1] is modified to
                         * substract the ']' in '...]'
                         * 
                         * BigInteger[] encrypted is a BigInteger
                         * array with a length equal to temp
                         */
                        
                        showMessage("\nEncrypted detected: " + message);
                        String[] temp = message.split(", ");
                        temp[0] = temp[0].subSequence(1, temp[0].length()).toString();
                        temp[temp.length - 1] = temp[temp.length - 1].subSequence(0, temp[temp.length - 1].length() - 1).toString();
                        encrypted = new BigInteger[temp.length];
                        for (int i = 0; i < encrypted.length; i++) {
                            encrypted[i] = new BigInteger(temp[i]);
                        }
                        
                        /**
                         * @note:
                         * Server ask the client to close RSA window after it
                         * gives the encrypted message and run the Client File
                         */
                        
                        sendData("Close this window and run Client Window");

                    } else {
                        showMessage("\n" + message);
                    }
                } else {

                    if (!ignore) {
                        
                        /**
                         * @note:
                         * this will go once to notify the client
                         * that it needs to get a S value
                         */
                        
                        sendData("please enter 's'");
                        ignore = true;
                        message = (String) input.readObject();
                        sendData("getting S");
                    }

                    if (checkInputS(message)) {
                        int s = input.readInt();
                        sendData("wait");
                        
                        /**
                         * @note:
                         * 
                         * checkInput(message)
                         * checks if message comes in format
                         * "Client >>> S: "
                         * 
                         * sendData("wait")
                         * notifies client to stay waiting
                         * 'til testing process ends
                         */
                        
                        if ("Client >>> FINISH".equals("" + s)) {
                            int answer = JOptionPane.showConfirmDialog(this, "Sure you want to finish connection?", "ATENTION", JOptionPane.YES_NO_OPTION);
                            if (answer != JOptionPane.YES_OPTION) {
                                message = "Refused LOGOUT";
                                showMessage(message);
                            }
                        }

                        numS = BigInteger.valueOf(s);
                        showMessage("\nCalculating with " + numS);
                        boolean x = check_S_Value();
                        if (!x) {
                            System.out.println("Failed Decryption " + numS);
                            sendData("Failed Decryption");
                        } else {
                            sendData("Decryption Success");
                        }
                    } else {
                        showMessage(message);
                    }
                }
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "\nUnknown kind of Object detected", "WARNING", JOptionPane.WARNING_MESSAGE);
            }

        } while (!message.equals("Client >>> FINISH"));
    }

    private boolean isBigInteger(String ms) {
        String checking = "";
        for (int i = 0; i < 4; i++) {
            checking = checking + ms.charAt(i);
        }
        return (checking.equals("Key:"));
    }

    private boolean checkInputS(String ms) {
        String checking = "";
        showMessage("\nTaking S");
        for (int i = 11; i < 14; i++) {
            checking = checking + ms.charAt(i);
        }

        return (checking.equals("S: "));
    }

    private boolean check_S_Value() {
        showMessage("\nTrying");
        BigInteger c, numD, dc;

        int i = 1;
        int e = numE.intValue();
        c = encrypted[0].multiply(numS.modPow(numE, numN));
        do {
            numD = BigInteger.valueOf(i);
            dc = c.modPow(numD, numN);
            if ((dc.multiply(numS)).compareTo(numD) == 0) {
                
                /**
                 * @note:
                 * 
                 * if decripted message (dc) * s == d
                 * returns true
                 */
                
                return true;
            }
            i++;
            System.out.println("e: " + e + " || i: " + i);
        } while (i < e);

        return false;
    }

    private void closeConnection() {
        showMessage("\nLogout\n");
        setTextFieldEditable(false);

        try {
            output.close();
            input.close();
            socketClient.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private void closeServer() {
        try {
            showMessage("Closing Server");
            server.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendData(String ms) {
        try {
            output.writeObject("Server >>> " + ms);
            output.flush();
            showMessage("\nServer >>> " + ms);
        } catch (IOException ex) {
            screenArea.append("\nFatal Error trying to write Object");
        }
    }

    private void showMessage(String ms) {
        SwingUtilities.invokeLater(
                new Runnable() {
            @Override
            public void run() {
                screenArea.append(ms);
                screenArea.setCaretPosition(
                        screenArea.getText().length());
            }
        });
    }

    private void setTextFieldEditable(final boolean editable) {
        SwingUtilities.invokeLater(
                new Runnable() {
            @Override
            public void run() {
                textField.setEditable(editable);
            }
        });
    }

    public static void main(String[] args) {
        Server server = new Server();
    }
}
