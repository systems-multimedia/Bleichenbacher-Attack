/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rsa;

import java.math.BigInteger;
import javax.swing.JOptionPane;
import java.net.*;
import java.io.*;
import java.util.Arrays;

/**
 *
 * @author Sam
 */
public class Enc_Window extends Thread {

    // declaring variables
    private Enc_Window app;
    private Form form;
    private String title = "Client";

    private final String chatServer;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket socketClient;

    // init Constructors
    public Enc_Window(final Form form) {
        this.chatServer = "127.0.0.1";
        form.setTitle("Client Form");
        this.form = form;
        form.setVisible(true);
    }

    public Enc_Window(Enc_Window app) {
        this.chatServer = "127.0.0.1";
        this.app = app;
        this.app.getForm().setApp(this);
        run_Enc_Window();
    }
    
    // end of Constructors

    public static void main(String args[]) {
        /**
         * through app, there's a thread to work on window
         * then, appCommit works in a new thread to catch connections
         */
        Enc_Window app = new Enc_Window(new Form());
        Enc_Window appCommit = new Enc_Window(app);
    }

    // init Methods
    
    private void run_Enc_Window() {
        try {

            connect_w_Server();
            getFlows();
            processConnection();
        } catch (EOFException e) {
            JOptionPane.showMessageDialog(form, "Connection Closed", "WARNING", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(form, "Run Server and Restart Window", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        } finally {
            closeConnection();
        }

    }

    private void connect_w_Server() throws IOException {
        showMessage("Trying to connect");
        socketClient = new Socket(InetAddress.getByName(chatServer), 12345);
        /**
         * if there's not a server running, this will throw
         * an IOException
         */
        title = title + " || " + socketClient.getInetAddress().getHostName();
        app.getForm().setTitle(title);
    }

    private void getFlows() throws IOException {
        input = new ObjectInputStream(socketClient.getInputStream());   // to receive server messages
        output = new ObjectOutputStream(socketClient.getOutputStream());// to send messages to server
        output.flush(); // Clean Output Stream
        showMessage("I/O Flows gotten successfully");
    }

    private void processConnection() throws IOException {

        String message = "";
        int cont = 0;
        sendData("Trying Connection");          // Test Message
        do {
            try {
                message = (String) input.readObject();
                cont++;
                app.getForm().setTitle(app.getForm().getTitle() + " (" + cont + ")"); // Notifies a message entry through the title
                showMessage("SERVER", message); // Specific method to show server messages
                app.getForm().setTitle(title);
                cont = 0;

            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(app.getForm(), "\nUnknown kind of Object detected", "WARNING", JOptionPane.WARNING_MESSAGE);
            } catch (NullPointerException ex) {
                JOptionPane.showMessageDialog(app.getForm(), "Null", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        } while (!message.equals("Server >>> FINISH"));
    }

    public void sendData(BigInteger key, BigInteger mode, Enc_Window con) {
        // method to send public key, with mode n
        String mensaje = "Key: " + key.toString() + " mode: " + mode;
        ObjectOutputStream out = con.getOutput();
        try {
            out.writeObject(mensaje);
            out.flush();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(form, ex.getMessage(), "WARNING", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void sendData(BigInteger[] ms) {
        // method to send encrypted message
        try {
            output.writeObject(Arrays.toString(ms));
            output.flush();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(form, ex.getMessage(), "WARNING", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void sendData(String ms) {
        // method to send usual messages
        try {
            output.writeObject("Client >>> " + ms);
            output.flush();
            showMessage("Client >>> " + ms);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(form, "Fatal Error trying to write Object", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMessage(String ms) {
        // method to send internal messages
        JOptionPane.showMessageDialog(form, ms, "WARNING", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showMessage(String title, String ms) {
        // method to show server messages
        JOptionPane.showMessageDialog(form, ms, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public void closeConnection() {
        System.out.println("Logout\n");

        try {
            this.output.close();
            this.input.close();
            socketClient.close();
        } catch (IOException ex) {
        }   // DO_NOTHING

    }

    public Form getForm() {
        return this.form;
    }

    public ObjectOutputStream getOutput() {
        return output;
    }

}
