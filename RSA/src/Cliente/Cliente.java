package Cliente;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.util.EventListener;
import javax.swing.*;

public class Cliente extends JFrame implements EventListener {

    private JTextField CampoIntroducir;
    private JTextArea areaPantalla;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private String mensaje = "";
    private String servidorChat;
    private Socket cliente;
    private Container container;
    private Thread thread;

    public Cliente(final String host) {

        super("Cliente");

        container = getContentPane();

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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
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
        mostrarMensaje("\nIntentando ralizar conexión\n");
        cliente = new Socket(InetAddress.getByName(servidorChat), 12345);
        mostrarMensaje("\nConectado a: "
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
                if (mensaje.equals("Servidor >>> please enter 's'")) {
                    mensaje = JOptionPane.showInputDialog(this, "Please enter 's'");
                    if (!mensaje.isEmpty()) {
                        enviarDatos("S: " + mensaje);
                    } else {
                        enviarDatos("S: ");
                        int i = 1;
                        boolean end = false;
                        do {
                            if (!(mensaje = (String) entrada.readObject()).equals("Servidor >>> wait")) {
                                mostrarMensaje("\n" + mensaje);
                                mostrarMensaje("\nsend 'TERMINAR' to finish process");
                                enviarDatos(i);
                                mensaje = (String) entrada.readObject();

                                switch (mensaje) {
                                    case "Servidor >>> Decryption Success":
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
                                mensaje = "Servidor >>> TERMINAR";
                            }
                        }
                    }
                }

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

    private void enviarDatos(int mensaje) {
        try {
            salida.writeInt(mensaje);
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

    public Socket getCliente() {
        return cliente;
    }

    public void setCliente(Socket cliente) {
        this.cliente = cliente;
        System.out.println("Intentando Conexión");
        //JOptionPane.showMessageDialog(this, "Intentando Conexion", "AVISO", JOptionPane.INFORMATION_MESSAGE);
    }

    public String getServidorChat() {
        return servidorChat;
    }

    public static void main(String[] args) {
        Cliente cliente = new Cliente("127.0.0.1");
        cliente.EjecutarCliente();
    }

}
