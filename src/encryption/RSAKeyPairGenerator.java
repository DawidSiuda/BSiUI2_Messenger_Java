package encryption;

import model.RSAKeyPair;
import model.RSAPrivateKey;
import model.RSAPublicKey;

import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

public class RSAKeyPairGenerator {
    private RSAKeyPairGenerator() {

    }

    public static RSAKeyPair generateKeyPair(int bitLength) {
        BigInteger bigNumberPrivateExponent = BigInteger.ZERO;
        BigInteger bigNumberModulo = BigInteger.ZERO;
        BigInteger bigNumberPublicExponent = BigInteger.ZERO;;
        while(bigNumberPrivateExponent.equals(BigInteger.ZERO)) {
            BigInteger bigNumberP = BigInteger.probablePrime(bitLength, new Random());
            BigInteger bigNumberQ = BigInteger.probablePrime(bitLength, new Random());

            BigInteger bigNumberPhi = bigNumberP.subtract(BigInteger.ONE).multiply(bigNumberQ.subtract(BigInteger.ONE));
            bigNumberModulo = bigNumberP.multiply(bigNumberQ);

            bigNumberP = null;
            bigNumberQ = null;

            bigNumberPublicExponent = generateRelativePrimeNumber(bigNumberPhi);
            bigNumberPrivateExponent = generateModularMultiplicativeInverse(bigNumberPublicExponent, bigNumberPhi);
        }
        return new RSAKeyPair(bigNumberPrivateExponent, bigNumberPublicExponent, bigNumberModulo);
    }

    private static BigInteger generateRelativePrimeNumber(BigInteger number) {
        BigInteger e = new BigInteger("3");
        BigInteger bigIntegerTwo = new BigInteger("2");
        for(; !e.gcd(number).equals(BigInteger.ONE); e = e.add(bigIntegerTwo));
        return e;
    }

    private static BigInteger generateModularMultiplicativeInverse(BigInteger bigNumberPublicExponent, BigInteger bigNumberPhi) {
        BigInteger u, w, x, z, q;

        u = BigInteger.ONE; w = bigNumberPublicExponent;
        x = BigInteger.ZERO; z = bigNumberPhi;
        while( w.compareTo(BigInteger.ZERO) > 0 ) {
            if( w.compareTo(z) < 0 ) {
                q = u; u = x; x = q;
                q = w; w = z; z = q;
            }
            q = w.divide(z);
            u = u.subtract(q.multiply(x));
            w = w.subtract(q.multiply(z));
        }
        if( z.equals(BigInteger.ONE) )
            if( x.compareTo(BigInteger.ZERO) < 0 )
                return x.add(bigNumberPhi);
        return BigInteger.ZERO;
    }

    public static void main(String[] args) {
        String option = "3";
        while (!option.equals("0")) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Choose option:\n1. Encrypt\n2. Decrypt\n3. Generate key pair\n4. Check if encryption works\n0. End program");
            option = scanner.nextLine();
            if(option.contentEquals("1")) {
                String e, n, messageToEncrypt;
                System.out.println("E: ");
                e = scanner.nextLine();
                System.out.println("N: ");
                n = scanner.nextLine();
                System.out.println("Message to encrypt: ");
                messageToEncrypt = scanner.nextLine();

                RSAPublicKey rsaPublicKey = new RSAPublicKey(new BigInteger(e), new BigInteger(n));
                Encoder encoder = new Encoder(rsaPublicKey);
                System.out.println(encoder.encryptMessage(messageToEncrypt));
            } else if(option.contentEquals("2")) {
                String d, n, messageToDecrypt;
                System.out.println("D: ");
                d = scanner.nextLine();
                System.out.println("N: ");
                n = scanner.nextLine();
                System.out.println("Message to decrypt: ");
                messageToDecrypt = scanner.nextLine();

                RSAPrivateKey rsaPrivateKey = new RSAPrivateKey(new BigInteger(d), new BigInteger(n));
                Decoder decoder = new Decoder(rsaPrivateKey);
                String decryptedMessage = decoder.decodeMessage(messageToDecrypt);
                System.out.println(decryptedMessage);
            }
            else if(option.contentEquals("3")) {
                System.out.println("Provide key length: ");
                String keyLength = scanner.nextLine();
                RSAKeyPair rsaKeyPair = RSAKeyPairGenerator.generateKeyPair(Integer.parseInt(keyLength));
                System.out.println("N:\n" + rsaKeyPair.getPublicKey().getN());
                System.out.println("E:\n" + rsaKeyPair.getPublicKey().getE());
                System.out.println("D:\n" + rsaKeyPair.getPrivateKey().getD());
            }
            else if(option.contentEquals("4")) {
                RSAKeyPair rsaKeyPair = RSAKeyPairGenerator.generateKeyPair(1024);
                String testMessage = "{\"login\":\"user\",\"password\":\"password\",\"userPublicKey\":{\"e\":3,\"n\":123}}";
                Encoder encoder = new Encoder(rsaKeyPair.getPublicKey());
                Decoder decoder = new Decoder(rsaKeyPair.getPrivateKey());
                System.out.println(decoder.decodeMessage(encoder.encryptMessage(testMessage)).equals(testMessage));
            }
        }
    }
}
