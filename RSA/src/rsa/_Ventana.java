/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rsa;

import Cliente.Cliente;
import java.math.BigInteger;
import javax.swing.JOptionPane;
import java.net.*;
import java.io.*;
import javax.swing.JFrame;

/**
 *
 * @author Sam
 */
public class _Ventana extends Thread{
    
    private _Ventana app;
    private Ventana ventana;
    private Cliente client;
    private String title = "Client";
    
    private String servidorChat="127.0.0.1";
    private ObjectInputStream entrada;
    private ObjectOutputStream salida;
    private Socket cliente;
    
    public _Ventana(final Ventana ventana){
        ventana.setTitle("Cliente");
        this.ventana = ventana;
        ventana.setVisible(true);
    }
    
    public _Ventana(_Ventana app){
        this.app = app;
        this.app.getVentana().setVentana(this);
        EjecutarCliente();
    }
    
    public static void main(String args[]) {
        _Ventana app = new _Ventana(new Ventana());
        _Ventana appCommit = new _Ventana(app);
    }
    
    private void EjecutarCliente() {
        try {

            ConectarServidor();
            obtenerFlujos();
            procesarConexion();
        } catch (EOFException e) {
            JOptionPane.showMessageDialog(ventana, "El Cliente cerró la Conexión", "AVISO", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(ventana, "Saliendo", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        } finally {
            cerrarConexion();
        }
        
    }

    private void ConectarServidor() throws IOException {
        mostrarMensaje("Intentando ralizar conexión");
        cliente = new Socket(InetAddress.getByName(servidorChat), 12345);
        title = title + " || " + cliente.getInetAddress().getHostName();
        app.getVentana().setTitle(title);
    }
    
    private void obtenerFlujos() throws IOException {
        entrada = new ObjectInputStream(cliente.getInputStream());
        salida = new ObjectOutputStream(cliente.getOutputStream());
        salida.flush();
        mostrarMensaje("Se recibieron los flujos de E/S");
    }
    
    private void procesarConexion() throws IOException {

        String mensaje = "";
        int cont = 0;
        enviarDatos("Hola!");
        do {
            try {

                mensaje = (String) entrada.readObject();
                cont++;
                app.getVentana().setTitle(app.getVentana().getTitle() + " (" + cont + ")");
                mostrarMensaje("SERVIDOR", mensaje);
                app.getVentana().setTitle(title);

            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(app.getVentana(), "\nSe recibió un tipo de objeto desconocido", "AVISO", JOptionPane.WARNING_MESSAGE);
            } catch (NullPointerException ex) {
                JOptionPane.showMessageDialog(app.getVentana(), "Null", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        } while (!mensaje.equals("Servidor >>> TERMINAR"));
    }
    
    public void enviarDatos(BigInteger data, _Ventana con){
        String mensaje = "Key: " + data.toString();
        ObjectOutputStream salida = con.getSalida();
        try {
            salida.writeObject(mensaje);
            salida.flush();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(ventana, ex.getMessage(), "AVISO", JOptionPane.ERROR_MESSAGE);
        } 
    }
    
    private void enviarDatos(String mensaje) {
        try {
            salida.writeObject("Cliente >>> " + mensaje);
            salida.flush();
            mostrarMensaje("Cliente >>> " + mensaje);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(ventana, "Error al escribir objeto", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mostrarMensaje(String mensaje){
        JOptionPane.showMessageDialog(ventana, mensaje, "AVISO", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarMensaje(String title, String mensaje){
        JOptionPane.showMessageDialog(ventana, mensaje, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void cerrarConexion() {
        System.out.println("Finalizando la Conexion\n");

        try {
            this.salida.close();
            this.entrada.close();
            cliente.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }

    public Ventana getVentana(){
        return this.ventana;
    }

    public ObjectOutputStream getSalida() {
        return salida;
    }
    
    
    
}


