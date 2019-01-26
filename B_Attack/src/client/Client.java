package client;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.util.EventListener;
import javax.swing.*;

public class Client extends JFrame implements EventListener {

    private JTextField textField;
    private JTextArea screenArea;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private String chatServer;
    private Socket client;
    private Container container;

    public Client(final String host) {

        super("Cliente");

        container = getContentPane();

        chatServer = host;

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
        container.add((new JScrollPane(screenArea)), BorderLayout.CENTER);
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void runClient() {

        try {
            connect_w_Server();
            getFlows();
            processConnection();
        } catch (EOFException e) {
            JOptionPane.showMessageDialog(this, "Connection Closed", "WARNING", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void connect_w_Server() throws IOException {
        showMessage("\nTrying to Connect\n");
        client = new Socket(InetAddress.getByName(chatServer), 12345);
        showMessage("\nConnected to: "
                + client.getInetAddress().getHostName());
    }

    private void getFlows() throws IOException {
        output = new ObjectOutputStream(client.getOutputStream());
        output.flush();

        input = new ObjectInputStream(client.getInputStream());
        showMessage("\nI/O Flows gotten successfully");
    }

    private void processConnection() throws IOException {

        setTextFieldEditable(true);

        do {
            try {

                message = (String) input.readObject();
                showMessage("\n" + message);
                if (message.equals("Server >>> please enter 's'")) {

                    sendData("S: ");
                    int i = 1;
                    boolean end = false;
                    do {
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
                    } while (i < 1000 && end == false);
                    if (!end) {
                        int answer = JOptionPane.showConfirmDialog(this, "After many connections, haven't found a compatible value \nContinue?", "Connection Failed", JOptionPane.YES_NO_OPTION);
                        if (answer != JOptionPane.YES_OPTION) {
                            message = "Server >>> FINISH";
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
            ex.printStackTrace();
        }

    }

    private void sendData(String ms) {
        try {
            output.writeObject("Client >>> " + ms);
            output.flush();
            showMessage("\nClient >>> " + ms);
        } catch (IOException ex) {
            screenArea.append("\nFatal Error trying to write Object");
        }
    }

    private void sendData(int ms) {
        try {
            output.writeInt(ms);
            output.flush();
            showMessage("\nClient >>> " + ms);
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
