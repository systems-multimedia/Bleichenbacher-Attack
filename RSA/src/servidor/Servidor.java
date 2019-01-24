package servidor;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import rsa._Ventana;

public class Servidor extends JFrame{

    private JTextField CampoIntroducir;
    private JTextArea areaPantalla;
    private ObjectOutputStream salida, salida2;
    private ObjectInputStream entrada, entrada2;
    private ServerSocket servidor;
    private Socket conexion, conexion2;
    private int contador = 1;
    
    private Thread thread;
    
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
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void EjecutarServidor(final Thread thread) {
        this.thread = thread;
        this.thread.setName("Server");
        this.thread.start();
        
        System.out.println("Executing " + this.thread.getName() + " at " + ((System.currentTimeMillis()/1000000000)/1000) + "seg");
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
        System.out.println("Esperando conexion en " + thread.getName() + " at " + ((System.currentTimeMillis()/1000000000)/100) + "seg");
        conexion = servidor.accept();
        conexion2 = servidor.accept();
        mostrarMensaje("Conexion " + contador + " recibida de: "
                + conexion.getInetAddress().getHostName());
        mostrarMensaje("Conexion " + contador + " recibida de: "
                + conexion2.getInetAddress().getHostName());
    }

    private void obtenerFlujos() throws IOException {
        salida = new ObjectOutputStream(conexion.getOutputStream());
        salida.flush();
        
        salida2 = new ObjectOutputStream(conexion2.getOutputStream());
        salida2.flush();

        entrada = new ObjectInputStream(conexion.getInputStream());
        entrada2 = new ObjectInputStream(conexion2.getInputStream());
        mostrarMensaje("\nSe recibieron los flujos de E/S");
    }

    private void procesarConexion() throws IOException {

        boolean seguir = false;
        BigInteger[] encriptado = null;
        BigInteger numS, numE;

        String mensaje = "Conexión Exitosa";
        enviarDatos(mensaje);
        mensaje = "Indique Encriptado, luego indique 'Continuar'";
        enviarDatos(mensaje);
        establecerCampoTextoEditable(true);
        do {
            try {
                if (!seguir) {
                    mensaje = (String) entrada.readObject();
                    if (mensaje == "Cliente >>> Continuar") {
                        seguir = true;
                    } else {
                        mostrarMensaje("\n" + mensaje);
                        encriptado = new BigInteger[mensaje.length()];
                        String[] temp = mensaje.split(" ");
                        for (int i = 0; i < encriptado.length; i++) {
                            encriptado[i] = new BigInteger(temp[i]);
                        }
                    }
                } else {
                    mensaje = "Indique valor s";
                    enviarDatos(mensaje);
                    numS = new BigInteger((String) entrada.readObject());
                    mensaje = "Indique valor e";
                    enviarDatos(mensaje);
                    numE = new BigInteger((String) entrada.readObject());
                    if (!probarS(encriptado, numS, numE)) {
                        mensaje = "Intento Fallido";
                        enviarDatos(mensaje);
                    } else {
                        mensaje = "S entra en el rango";
                        enviarDatos(mensaje);
                    }
                }
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "\nSe recibió un tipo de objeto desconocido", "AVISO", JOptionPane.WARNING_MESSAGE);
            }
        } while (!mensaje.equals("Cliente >>> TERMINAR"));
    }

    private boolean revisarBigInteger(String mensaje){
        String checking = "";
        
        for(int i=0; i<11; i++){
            checking = checking + mensaje.charAt(i);
        }
        
        return (checking.equals("Big Integer"));
    }
    
    private boolean probarS(BigInteger[] c, BigInteger s, BigInteger e) {
        BigInteger[] Cp = new BigInteger[c.length];

        for (int i = 0; i < Cp.length; i++) {
            Cp[i] = c[i].multiply(s.modPow(e, e));
        }
        
        return false;
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

    public ServerSocket getServidor() {
        return servidor;
    }

    public void setServidor(ServerSocket servidor) {
        this.servidor = servidor;
        JOptionPane.showMessageDialog(this, "Intentando Conexion", "AVISO", JOptionPane.INFORMATION_MESSAGE);
    }

    public Socket getConexion() {
        return conexion;
    }

    public void setConexion(Socket conexion) {
        this.conexion = conexion;
    }

    public static void main(String[] args){
        Servidor server = new Servidor();
        
        server.EjecutarServidor(new Thread("Server"));
    }
}
