package Cliente;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class Cliente extends JFrame{

    private JTextField CampoIntroducir;
    private JTextArea areaPantalla;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private String mensaje = "";
    private String servidorChat;
    private Socket cliente;
    
    private Thread thread;

    public Cliente(final String host, final Thread thread) {

        super("Cliente");

        Container container = getContentPane();
        
        servidorChat = host;
        
        CampoIntroducir = new JTextField();
        CampoIntroducir.setEditable(false);
        CampoIntroducir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                enviarDatos(evento.getActionCommand());
                CampoIntroducir.setText("");
            }
        });

        container.add(CampoIntroducir, BorderLayout.NORTH);

        areaPantalla = new JTextArea();
        container.add((new JScrollPane(areaPantalla)), BorderLayout.CENTER);
        setSize(300, 150);
        setVisible(true);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
        this.thread = thread;
    }
    public void EjecutarCliente() {
        try {

            ConectarServidor();
            obtenerFlujos();
            procesarConexion();
        } catch (EOFException e) {
            JOptionPane.showMessageDialog(this, "El Cliente cerró la Conexión", "AVISO", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            cerrarConexion();
        }

    }

    private void ConectarServidor() throws IOException {
        mostrarMensaje("Intentando ralizar conexión\n");
        System.out.println("Esperando conexion en " + thread.getName() + " at " + (System.currentTimeMillis()/1000000000));
        cliente = new Socket(InetAddress.getByName(servidorChat), 12345);
        mostrarMensaje("Conectado a: "
                + cliente.getInetAddress().getHostName());
    }

    private void obtenerFlujos() throws IOException {
        salida = new ObjectOutputStream(cliente.getOutputStream());
        salida.flush();

        entrada = new ObjectInputStream(cliente.getInputStream());
        mostrarMensaje("\nSe recibieron los flujos de E/S");
    }

    private void procesarConexion() throws IOException {

        establecerCampoTextoEditable(true);

        do {
            try {

                mensaje = (String) entrada.readObject();
                mostrarMensaje("\n" + mensaje);

            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "\nSe recibió un tipo de objeto desconocido", "AVISO", JOptionPane.WARNING_MESSAGE);
            }
        } while (!mensaje.equals("Servidor >>> TERMINAR"));
    }

    private void cerrarConexion() {
        mostrarMensaje("\nFinalizando la Conexion\n");
        establecerCampoTextoEditable(false);

        try {
            salida.close();
            entrada.close();
            cliente.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }

    private void enviarDatos(String mensaje) {
        try {
            salida.writeObject("Cliente >>> " + mensaje);
            salida.flush();
            mostrarMensaje("\nCliente >>> " + mensaje);
        } catch (IOException ex) {
            areaPantalla.append("\nError al escribir objeto");
        }
    }

    private void mostrarMensaje(String mensaje) {
        SwingUtilities.invokeLater(
                new Runnable() {
            @Override
            public void run() {
                areaPantalla.append(mensaje);
                areaPantalla.setCaretPosition(
                        areaPantalla.getText().length());
            }
        });
    }

    private void establecerCampoTextoEditable(final boolean editable) {
        SwingUtilities.invokeLater(
                new Runnable() {
            @Override
            public void run() {
                CampoIntroducir.setEditable(editable);
            }
        });
    }


}
