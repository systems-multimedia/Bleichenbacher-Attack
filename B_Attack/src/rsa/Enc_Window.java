/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rsa;

import client.Client;
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

    private Enc_Window app;
    private Form form;
    private String title = "Client";

    private String chatServer = "127.0.0.1";
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket socketClient;

    public Enc_Window(final Form form) {
        form.setTitle("Client Form");
        this.form = form;
        form.setVisible(true);
    }

    public Enc_Window(Enc_Window app) {
        this.app = app;
        this.app.getForm().setApp(this);
        run_Enc_Window();
    }

    public static void main(String args[]) {
        Enc_Window app = new Enc_Window(new Form());
        Enc_Window appCommit = new Enc_Window(app);
    }

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
        title = title + " || " + socketClient.getInetAddress().getHostName();
        app.getForm().setTitle(title);
    }

    private void getFlows() throws IOException {
        input = new ObjectInputStream(socketClient.getInputStream());
        output = new ObjectOutputStream(socketClient.getOutputStream());
        output.flush();
        showMessage("I/O Flows gotten successfully");
    }

    private void processConnection() throws IOException {

        String message = "";
        int cont = 0;
        sendData("Trying Connection");
        do {
            try {

                message = (String) input.readObject();
                cont++;
                app.getForm().setTitle(app.getForm().getTitle() + " (" + cont + ")");
                showMessage("SERVER", message);
                app.getForm().setTitle(title);

            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(app.getForm(), "\nUnknown kind of Object detected", "WARNING", JOptionPane.WARNING_MESSAGE);
            } catch (NullPointerException ex) {
                JOptionPane.showMessageDialog(app.getForm(), "Null", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        } while (!message.equals("Server >>> FINISH"));
    }

    public void sendData(BigInteger key, BigInteger mode, Enc_Window con) {
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
        try {
            output.writeObject(Arrays.toString(ms));
            output.flush();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(form, ex.getMessage(), "WARNING", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void sendData(String ms) {
        try {
            output.writeObject("Client >>> " + ms);
            output.flush();
            showMessage("Client >>> " + ms);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(form, "Fatal Error trying to write Object", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMessage(String ms) {
        JOptionPane.showMessageDialog(form, ms, "WARNING", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showMessage(String title, String ms) {
        JOptionPane.showMessageDialog(form, ms, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public void closeConnection() {
        System.out.println("Logout\n");

        try {
            this.output.close();
            this.input.close();
            socketClient.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public Form getForm() {
        return this.form;
    }

    public ObjectOutputStream getOutput() {
        return output;
    }

}
