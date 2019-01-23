package Cliente;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Cliente extends JFrame {

    private JTextField CampoIntroducir;
    private JTextArea areaPantalla;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private String mensaje = "";
    private String servidorChat;
    private Socket cliente;

    public Cliente(String host) {

        super("Cliente");

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
        container.add((new JScrollPane(areaPantalla)), BorderLayout.CENTER);
        setSize(300, 150);
        setVisible(true);
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

    public static void main(String[] args) {
        Cliente aplicacion;

        if (args.length == 0) {
            aplicacion = new Cliente("127.0.0.1");
        } else {
            aplicacion = new Cliente(args[0]);
        }
        
        aplicacion.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        aplicacion.setLocationRelativeTo(null);
        aplicacion.EjecutarCliente();
    }

}
