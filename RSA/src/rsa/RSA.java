package rsa;

import java.math.BigInteger;
import java.util.Random;


public class RSA {

    int TamañoPrimo;
    private BigInteger Nump, Numq, Numn;
    private BigInteger NumPhi;
    private BigInteger Nume, Numd;
    private Ventana ventana;
    private _Ventana conection;
    
    public RSA(int TamañoPrimo, final _Ventana conection, final Ventana ventana) {
        this.TamañoPrimo = TamañoPrimo;
        this.ventana = ventana;
        this.conection = conection;
        GenerarPrimos();
        
    }

    public RSA(int TamañoPrimo, BigInteger Nump, BigInteger Numq, final _Ventana conection, final Ventana ventana) {
        this.TamañoPrimo = TamañoPrimo;
        this.ventana = ventana;
        this.conection = conection;
        this.Nump = Nump;
        this.Numq = Numq;
        GenerarClaves();
        
    }

    public void GenerarPrimos() {
        Nump = new BigInteger(TamañoPrimo, 10, new Random());
        do {
            Numq = new BigInteger(TamañoPrimo, 10, new Random());
        } while (Numq.compareTo(Nump) == 0);
    }

    public void GenerarClaves() {
        Numn = Nump.multiply(Numq);
        NumPhi = (Nump.subtract(BigInteger.valueOf(1))).multiply(Numq.subtract(BigInteger.valueOf(1)));

        do {
            Nume = new BigInteger(2 * TamañoPrimo, new Random());
        } while (Nume.compareTo(NumPhi) != -1 || Nume.gcd(NumPhi).compareTo(BigInteger.valueOf(1)) != 0);

        Numd = Nume.modInverse(NumPhi);
        conection.enviarDatos(Nume, conection);
    }

    public BigInteger[] Encriptar(String mensaje) {
        byte[] temp = new byte[1];
        byte[] digitos = mensaje.getBytes();
        BigInteger[] BigDigitos = new BigInteger[digitos.length];

        for (int i = 0; i < BigDigitos.length; i++) {
            temp[0] = digitos[i];
            BigDigitos[i] = new BigInteger(temp);
        }

        BigInteger[] encriptado = new BigInteger[BigDigitos.length];
        for (int i = 0; i < BigDigitos.length; i++) {
            
            encriptado[i] = BigDigitos[i].modPow(Nume, Numn);
        }

        return encriptado;
    }

    public String Desencriptar(BigInteger[] encriptado) {
        BigInteger[] desencriptado = new BigInteger[encriptado.length];
        for (int i = 0; i < desencriptado.length; i++) {
            desencriptado[i] = encriptado[i].modPow(Numd, Numn);
        }

        char[] ArregloChar = new char[desencriptado.length];

        for (int i = 0; i < ArregloChar.length; i++) {
            ArregloChar[i] = (char) desencriptado[i].intValue();
        }

        return new String(ArregloChar);
    }

    public BigInteger getNump() {
        return Nump;
    }

    public BigInteger getNumq() {
        return Numq;
    }

    public BigInteger getNumn() {
        return Numn;
    }

    public BigInteger getNumPhi() {
        return NumPhi;
    }

    public BigInteger getNume() {
        return Nume;
    }

    public BigInteger getNumd() {
        return Numd;
    }

}
