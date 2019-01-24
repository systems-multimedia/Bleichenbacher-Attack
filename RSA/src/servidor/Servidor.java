package servidor;

import Cliente.Cliente;
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
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private ServerSocket servidor;
    private Socket conexion;
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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        EjecutarServidor(new Thread("Server"), true);
    }

    public void EjecutarServidor(final Thread thread, boolean repeat) {
        this.thread = thread;
        this.thread.setName("Server");
        this.thread.start();
        char res = 'y';
        
        System.out.println("Executing " + this.thread.getName() + " at " + ((System.currentTimeMillis()/1000000000)/1000) + "seg");
        try {
            servidor = new ServerSocket(12345, 100);

            while ((res == 'y' || res == 'Y')) {
                try {
                    esperarConexion();
                    obtenerFlujos();
                    procesarConexion();
                } catch (EOFException e) {
                    JOptionPane.showMessageDialog(this, "El Cliente cerró la Conexión", "AVISO", JOptionPane.WARNING_MESSAGE);
                } finally {
                    contador++;
                    if(!repeat){
                        if(JOptionPane.showConfirmDialog(this, "Desea Iniciar Nuevamente?", "ATENCIÓN", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                            res = 'y';
                        } else {
                            res = 'n';
                        }
                    } else {
                        repeat = false;
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("Desconectando");
            
            if(salida != null){
                cerrarConexion();
                cerrarServidor();
            }
            
            if(repeat){
                mostrarMensaje("Intentando Ejecutar Nuevamente");
                repeat = false;
                reiniciar();
                EjecutarServidor(new Thread(), repeat); 
            }
            contador++;
        }
        
        if(salida != null){
            cerrarConexion();
            cerrarServidor();
        }
    }
    
    private void reiniciar(){
        servidor = null;
        salida = null;
        entrada = null;
        conexion = null;
    }

    private void esperarConexion() throws IOException {
        mostrarMensaje("\nEsperando una Conexion\n");
        System.out.println("Esperando conexion en " + thread.getName() + " at " + ((System.currentTimeMillis()/1000000000)/100) + "seg");
        conexion = servidor.accept();
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
                if (!seguir) {
                    mensaje = (String) entrada.readObject();
                    if(revisarBigInteger(mensaje)){
                        String e = "";
                        for(int i=5; i<mensaje.length(); i++){
                            e = e + mensaje.charAt(i);
                        }
                        mostrarMensaje("\nClave Pública (" + e + ") Recibida");
                        numE = new BigInteger(e);
                    } else if (mensaje == "Cliente >>> Continuar") {
                        seguir = true;
                    } else {
                        mostrarMensaje("\n" + mensaje);
                        String[] temp = mensaje.split(" ");
                        encriptado = new BigInteger[temp.length];
                        for (int i = 0; i < encriptado.length; i++) {
                            String aux = temp[i];
                            if(Character.isDigit(aux.charAt(0))){
                                encriptado[i] = new BigInteger(aux);
                            }
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
        
        for(int i=0; i<4; i++){
            checking = checking + mensaje.charAt(i);
        }
        
        return (checking.equals("Key:"));
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
    
    private void cerrarServidor(){
        try {
            mostrarMensaje("CerrandoServidor");
            servidor.close();
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
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
    }
}
