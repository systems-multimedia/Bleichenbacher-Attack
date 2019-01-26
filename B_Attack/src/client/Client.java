package client;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.util.EventListener;
import javax.swing.*;

public class Client extends JFrame implements EventListener {

    // declaring variables
    
    private JTextField textField;
    private final JTextArea screenArea;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private final String chatServer;
    private Socket client;
    private final Container container;

    /**
     * end of Variables Declaration
     * 
     * init Constructor
     * @param host 
     */
    public Client(final String host) {

        super("Client");

        container = getContentPane();
        chatServer = host;
        textField = new JTextField();
        textField.setEditable(false);
        textField.addActionListener((ActionEvent ev) -> {
            sendData(ev.getActionCommand());
            textField.setText("");
        });

        container.add(textField, BorderLayout.NORTH);

        screenArea = new JTextArea();
        container.add((new JScrollPane(screenArea)), BorderLayout.CENTER);
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * end of Constructor
     * 
     * init methods
     */
    
    public void runClient() {

        try {
            connect_w_Server();
            getFlows();
            processConnection();
        } catch (EOFException e) {
            JOptionPane.showMessageDialog(this, "Connection Closed", "WARNING", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) { // DO_NOTHING
        } finally {
            closeConnection();
        }
    }

    private void connect_w_Server() throws IOException {
        showMessage("\nTrying to Connect\n");
        client = new Socket(InetAddress.getByName(chatServer), 12345);
        /**
         * if there's not a server running, this will throw
         * an IOException
         */
        setTitle(this.getTitle() + " || " + client.getInetAddress().getHostName());
        showMessage("\nConnected to: "
                + client.getInetAddress().getHostName());
    }

    private void getFlows() throws IOException {
        output = new ObjectOutputStream(client.getOutputStream());  // to send messages to server
        output.flush();

        input = new ObjectInputStream(client.getInputStream());     // to receive server messages
        showMessage("\nI/O Flows gotten successfully");
    }

    private void processConnection() throws IOException {

        setTextFieldEditable(true);
        int bound = 1000, init = 0;

        do {
            try {

                message = (String) input.readObject();
                showMessage("\n" + message);
                if (message.equals("Server >>> please enter 's'")) {
                    /**
                     * if server makes a S value request
                     * start this process
                     * 
                     * sendData("S: ")
                     * notifies server that client is going to
                     * send s values
                     */
                    sendData("S: ");
                    int i = init + 1;
                    boolean end = false;
                    do {
                        /**
                         * 'til server notifies that checking process is finished,
                         * Client will be waiting running loop without actions
                         * 
                         * when checking process is finished, client sends
                         * the variable i that is growing one by one
                         * 
                         * once the message equals to "Decryption Success"
                         * boolean end will notify the program that
                         * it can continue
                         */
                        if (!(message = (String) input.readObject()).equals("Server >>> wait")) {
                            showMessage("\n" + message);
                            showMessage("\nwrite 'FINISH' to finish process");
                            sendData(i);
                            message = (String) input.readObject();

                            switch (message) {
                                case "Server >>> Decryption Success":
                                    end = true;
                                    break;
                                default:
                                    i = i + 1;
                                    break;
                            }
                        }
                    } while (i < bound && end == false);
                    if (!end) {
                        /**
                         * if process is finished by many connections,
                         * it will ask user if he wants to start again or not
                         * in YES_OPTION case,
                         * init = init + 999
                         * bound = bound + 1000
                         */
                        int answer = JOptionPane.showConfirmDialog(this, "After many connections, haven't found a compatible value \nContinue?", "Connection Failed", JOptionPane.YES_NO_OPTION);
                        if (answer != JOptionPane.YES_OPTION) {
                            message = "Server >>> FINISH";
                            init = init + 999;
                            bound = bound + 1000;
                        }
                    } else {
                        message = "Server >>> FINISH";
                    }
                }

            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "\nUnknown Kind of Object Detected", "WARNING", JOptionPane.WARNING_MESSAGE);
            }
        } while (!message.equals("Server >>> FINISH"));
        JOptionPane.showMessageDialog(this, "Decryption Success", "STOP", JOptionPane.INFORMATION_MESSAGE);
    }

    private void closeConnection() {
        showMessage("\nLogout\n");
        setTextFieldEditable(false);

        try {
            output.close();
            input.close();
            client.close();
        } catch (IOException ex) {
        }   // DO_NOTHING

    }

    private void sendData(String ms) {
        // method to send usual messages
        try {
            output.writeObject("Client >>> " + ms);
            output.flush();
            showMessage("\nClient >>> " + ms);
        } catch (IOException ex) {
            screenArea.append("\nFatal Error trying to write Object");
        }
    }

    private void sendData(int ms) {
        // method to send S value
        try {
            output.writeInt(ms);
            output.flush();
            showMessage("\nClient >>> " + ms);
        } catch (IOException ex) {
            screenArea.append("\nFatal Error trying to write Object");
        }
    }

    private void showMessage(String ms) {
        // method to show usual messages
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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                textField.setEditable(editable);
            }
        });
    }

    public static void main(String[] args) {
        Client cliente = new Client("127.0.0.1");
        cliente.runClient();
    }

}
