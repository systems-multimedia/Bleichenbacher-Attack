/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rsa;

import Cliente.Cliente;
import Cliente.initiateClient;
import java.math.BigInteger;
import javax.swing.JOptionPane;
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import servidor.Servidor;
import servidor.initiateServer;

/**
 *
 * @author Sam
 */
public class _Ventana extends Thread{
    
    private Ventana ventana;
    
    private String servidorChat="127.0.0.1";
    private ObjectInputStream entrada;
    private ObjectOutputStream salida;
    private Socket cliente;
    
    public _Ventana(final Ventana ventana){
        this.ventana = ventana;
        ventana.setVisible(true);
    }
    
    public _Ventana(BigInteger data){
        this.enviarDatos(data);
    }
    
    public _Ventana(){
    }
    
    public static void main(String args[]) {
        
        initiateServer servidor = new initiateServer();
        initiateClient cliente = new initiateClient("127.0.0.1");
        _Ventana app = new _Ventana(new Ventana());
        _Ventana appCommit = new _Ventana();
        
        new Runnable(){
            public void run(){
                servidor.getServer().EjecutarServidor();
            }
        };
        
        new Runnable(){
            public void run(){
                cliente.getClient().EjecutarCliente();
            }
        };
        
        new Runnable(){
            public void run(){
                appCommit.EjecutarCliente();
            }
        };
    }
    
    
    public void EjecutarCliente() {
        try {

            ConectarServidor();
            obtenerFlujos();
            procesarConexion();
        } catch (EOFException e) {
            JOptionPane.showMessageDialog(ventana, "El Cliente cerró la Conexión", "AVISO", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            cerrarConexion();
        }

    }

    private void ConectarServidor() throws IOException {
        mostrarMensaje("Intentando ralizar conexión");
        cliente = new Socket(InetAddress.getByName(servidorChat), 12345);
        mostrarMensaje("Conectado a: "
                + cliente.getInetAddress().getHostName());
    }
    
    private void obtenerFlujos() throws IOException {
        entrada = new ObjectInputStream(cliente.getInputStream());
        salida = new ObjectOutputStream(cliente.getOutputStream());
        salida.flush();
        mostrarMensaje("Se recibieron los flujos de E/S");
    }
    
    private void procesarConexion() throws IOException {

        String mensaje = "";
        do {
            try {

                mensaje = (String) entrada.readObject();
                mostrarMensaje("SERVIDOR", mensaje);

            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(ventana, "\nSe recibió un tipo de objeto desconocido", "AVISO", JOptionPane.WARNING_MESSAGE);
            }
        } while (!mensaje.equals("Servidor >>> TERMINAR"));
    }
    
    public void enviarDatos(BigInteger data){
        try {
            salida.writeObject("Big Integer: " + data);
            salida.flush();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(ventana, "Error al escribir objeto", "AVISO", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mostrarMensaje(String mensaje){
        JOptionPane.showMessageDialog(ventana, mensaje, "AVISO", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarMensaje(String title, String mensaje){
        JOptionPane.showMessageDialog(ventana, mensaje, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void cerrarConexion() {
        mostrarMensaje("Finalizando la Conexion\n");

        try {
            cliente.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }

}


