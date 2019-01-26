package rsa;

import java.math.BigInteger;
import java.util.Random;

/**
 * RSA Operation is explained in link below
 *
 * @@link https://simple.wikipedia.org/wiki/RSA_algorithm#Operation
 *
 */
public class RSA {

    // declaring Variables
    int primeSize;
    private BigInteger Nump, Numq, Numn;
    private BigInteger NumPhi;
    private BigInteger Nume, Numd;
    private final Enc_Window connection;

    /**
     * end of Variables declaration
     *
     *
     *
     * init Constructors
     *
     * @param primeSize
     * @param connection
     */
    public RSA(int primeSize, final Enc_Window connection) {
        this.primeSize = primeSize;
        this.connection = connection;
        getParameters();

    }

    public RSA(int primeSize, BigInteger Nump, BigInteger Numq, final Enc_Window connection) {
        this.primeSize = primeSize;
        this.connection = connection;
        this.Nump = Nump;
        this.Numq = Numq;
        getKeys();

    }
    /**
     * end of Constructors
     * 
     * init methods
     * 
     * random p and q values
     */
    public final void getParameters() {
        Nump = new BigInteger(primeSize, 10, new Random()); // Constructor ==> BigInteger(int, int, Random)
        do {
            Numq = new BigInteger(primeSize, 10, new Random()); // Constructor ==> BigInteger(int, int, Random)
        } while (Numq.compareTo(Nump) == 0);                // while q == p
    }

    /**
     * n = p * q
     * PhiN = (p-1)(q-1)
     */
    public final void getKeys() {
        Numn = Nump.multiply(Numq);
        NumPhi = (Nump.subtract(BigInteger.valueOf(1))).multiply(Numq.subtract(BigInteger.valueOf(1)));

        do {
            Nume = new BigInteger(2 * primeSize, new Random());

        } while (Nume.compareTo(NumPhi) != -1 || Nume.gcd(NumPhi).compareTo(BigInteger.valueOf(1)) != 0);

        Numd = Nume.modInverse(NumPhi);
        connection.sendData(Nume, Numn, connection); // use the caller window to make a connection and send the key and the mode
    }

    public BigInteger[] Encrypted(String ms) {
        byte[] temp = new byte[1];
        byte[] dig = ms.getBytes();
        BigInteger[] bigDigits = new BigInteger[dig.length];

        for (int i = 0; i < bigDigits.length; i++) {
            temp[0] = dig[i];
            bigDigits[i] = new BigInteger(temp);    // Constructor ==> BigInteger(byte[])
        }

        BigInteger[] encrypted = new BigInteger[bigDigits.length];
        for (int i = 0; i < bigDigits.length; i++) {

            encrypted[i] = bigDigits[i].modPow(Nume, Numn);

            /**
             * Being c the encrypted message and m the message, c = m^e (mode n)
             * with e as the key public
             */
        }

        return encrypted;
    }

    public String Decrypted(BigInteger[] encrypted) {
        BigInteger[] decrypted = new BigInteger[encrypted.length];
        for (int i = 0; i < decrypted.length; i++) {
            decrypted[i] = encrypted[i].modPow(Numd, Numn);

            /**
             * being m the decrypted and c the encrypted one, m = c^d with d as
             * the private key
             */
        }

        char[] charArray = new char[decrypted.length];

        for (int i = 0; i < charArray.length; i++) {
            charArray[i] = (char) decrypted[i].intValue();
        }

        return new String(charArray);
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
