package servidor;

import Cliente.Cliente;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
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
    private static boolean continuar = false;
    
    private BigInteger numE, numN, numD;
    private BigInteger[] encriptado = null;
    private BigInteger numS;
    
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
        EjecutarServidor(new Thread("Server"), false);
    }

    public void EjecutarServidor(final Thread thread, boolean decrypting) {
        this.thread = thread;
        this.thread.setName("Server");
        this.thread.start();
        char res = 'y';
        
        System.out.println("Executing " + this.thread.getName() + " at " + ((System.currentTimeMillis()/1000000000)/1000) + "seg");
        try {
            servidor = new ServerSocket(12345, 100);

            try {
                esperarConexion();
                obtenerFlujos();
                procesarConexion(decrypting);
            } catch (EOFException e) {
                JOptionPane.showMessageDialog(this, "El Cliente cerró la Conexión", "AVISO", JOptionPane.WARNING_MESSAGE);
            } 
        } catch (IOException ex) {
            System.out.println("Desconectando");
            
            if(salida != null){
                cerrarConexion();
                cerrarServidor();
            }
            
            if(JOptionPane.showConfirmDialog(this, "Ejecutar Servidor", "ATENCIÓN", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                mostrarMensaje("\nIntentando Ejecutar Nuevamente");
                reiniciar();
                EjecutarServidor(new Thread(), true); 
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

    private void procesarConexion(boolean decrypting) throws IOException {

        boolean ignore = false;

        String mensaje = "Conexión Exitosa";
        enviarDatos(mensaje);
        establecerCampoTextoEditable(true);
        do {
            try {
                if(!decrypting){
                    mensaje = (String) entrada.readObject();
                    if(revisarBigInteger(mensaje)){
                        String e = "", n = "";
                        boolean _e = true;
                        for(int i=5; i<mensaje.length(); i++){
                            if(mensaje.charAt(i) == ' '){
                                i += 7;
                                _e = false;
                            }
                            if(_e){
                                e = e + mensaje.charAt(i);
                            } else {
                                n = n + mensaje.charAt(i);
                            }
                        }
                        System.out.println("\nClave Pública (" + e + ") mode " + n + " Recibida");
                        numE = new BigInteger(e);
                        numN = new BigInteger(n);
                    } else if(Character.isDigit(mensaje.charAt(1))){
                        mostrarMensaje("\nEncriptado Recibido: " + mensaje);
                        String[] temp = mensaje.split(", ");
                        temp[0] = temp[0].subSequence(1, temp[0].length()).toString();
                        temp[temp.length-1] = temp[temp.length-1].subSequence(0, temp[temp.length-1].length()-1).toString();
                        encriptado = new BigInteger[temp.length];
                        for(int i = 0; i < encriptado.length ; i++) {
                            encriptado[i] = new BigInteger(temp[i]);
                        }
                        enviarDatos("Cierre esta ventana e Inicie como Cliente");
                        
                    }  else {
                        mostrarMensaje("\n" + mensaje);
                    }
                } else {
                    
                    if(!ignore){
                        enviarDatos("please enter 's'");
                        ignore = true;
                        mensaje = (String) entrada.readObject();
                        enviarDatos("Recibiendo S");
                    }
                    
                    if(checkInputS(mensaje) || ignore == false){
                        int s = entrada.readInt();
                        enviarDatos("wait");
                        numS = BigInteger.valueOf(s);
                        mostrarMensaje("Empezando a Calcular " + numS);
                        boolean x = checkS();
                        if(!x){
                            System.out.println("Failed Decryption " + numS);
                            enviarDatos("Failed Decryption");
                            ignore = false;
                        } else {
                            enviarDatos("Decryption Success");
                        }
                    } 
                } 
            }catch (ClassNotFoundException ex) {
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
    
    private boolean checkInputS(String mensaje){
        String checking = "";
        mostrarMensaje("\nRECIBIENDO S");
        for(int i = 12; i < 15; i++){
            checking = checking + mensaje.charAt(i);
        }
        
        return (checking.equals("S: "));
    }
    
    private boolean checkS(){
        mostrarMensaje("\nProbando");
        BigInteger c, numD, dc;
        
        int i = 1;
        int e = numE.intValue();
        c = encriptado[0].multiply(numS.modPow(numE, numN));
        do {
            numD = BigInteger.valueOf(i);
            dc = c.modPow(numD, numN);
            if((dc.multiply(numS)).compareTo(numD) == 0){
                return true;
            }
            i++;
            System.out.println("e: " + e + " || i: " + i);
        } while (i < e);
        
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
    
    public static void main(String[] args){
        Servidor server = new Servidor();
    }
}
