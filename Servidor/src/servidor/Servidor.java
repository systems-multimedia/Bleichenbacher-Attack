package servidor;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class Servidor extends JFrame {

    private JTextField CampoIntroducir;
    private JTextArea areaPantalla;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private ServerSocket servidor;
    private Socket conexion;
    private int contador = 1;

    public Servidor() {

        super("Servidor");

        Container container = getContentPane();

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
        container.add((new JScrollPane(areaPantalla)));
        setSize(300, 150);
        setVisible(true);
    }

    public void EjecutarServidor() {
        try {
            servidor = new ServerSocket(12345, 100);

            while (true) {
                try {
                    esperarConexion();
                    obtenerFlujos();
                    procesarConexion();
                } catch (EOFException e) {
                    JOptionPane.showMessageDialog(this, "El Servidor cerró la Conexión", "AVISO", JOptionPane.WARNING_MESSAGE);
                } finally {
                    cerrarConexion();
                    contador++;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void esperarConexion() throws IOException {
        mostrarMensaje("Esperando una Conexion\n");
        conexion = servidor.accept();
        mostrarMensaje("Conexion " + contador + " recibida de: "
                + conexion.getInetAddress().getHostName());
    }

    private void obtenerFlujos() throws IOException {
        salida = new ObjectOutputStream(conexion.getOutputStream());
        salida.flush();

        entrada = new ObjectInputStream(conexion.getInputStream());
        mostrarMensaje("\nSe recibieron los flujos de E/S");
    }

    private void procesarConexion() throws IOException {

        boolean seguir = false;
        BigInteger[] encriptado = null;
        BigInteger numS, numE;

        String mensaje = "Conexión Exitosa";
        enviarDatos(mensaje);
        establecerCampoTextoEditable(true);
        do {
            try {
                mensaje = (String) entrada.readObject();
                encriptado = new BigInteger[mensaje.length()];
                String[] temp = mensaje.split(" ");
                for (int i = 0; i < encriptado.length; i++) {
                    encriptado[i] = new BigInteger(temp[i]);
                }
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "\nSe recibió un tipo de objeto desconocido", "AVISO", JOptionPane.WARNING_MESSAGE);
            }
        } while (!mensaje.equals("Cliente >>> TERMINAR"));
    }

    private void cerrarConexion() {
        mostrarMensaje("\nFinalizando la Conexion\n");
        establecerCampoTextoEditable(false);

        try {
            salida.close();
            entrada.close();
            conexion.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private void enviarDatos(String mensaje) {
        try {
            salida.writeObject("Servidor >>> " + mensaje);
            salida.flush();
            mostrarMensaje("\nServidor >>> " + mensaje);
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

    public static void main(String[] args) {
        Servidor aplicacion = new Servidor();
        aplicacion.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        aplicacion.setLocationRelativeTo(null);
        aplicacion.EjecutarServidor();
    }

}
