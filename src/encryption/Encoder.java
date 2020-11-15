package encryption;

import model.RSAPublicKey;

import java.math.BigInteger;
import java.util.Base64;

public class Encoder {
    private final RSAPublicKey rsaPublicKey;
    private int maxMessageLength;

    public Encoder(RSAPublicKey rsaPublicKey) {
        this.rsaPublicKey = rsaPublicKey;
        this.maxMessageLength = rsaPublicKey.getN().toByteArray().length;
        if(maxMessageLength % 2 == 1)
            this.maxMessageLength -= 1;
    }

    public String encryptMessage(String message) {
        byte[] encryptedMessage = new byte[0];
        String[] messageBlocks = message.split("(?<=\\G.{" + maxMessageLength + "})");
        for (String messageBlock : messageBlocks) {
            byte[] encryptedBlock = encryptBlock(messageBlock);
            encryptedMessage = joinBlocks(encryptedMessage, encryptedBlock);
        }
        return new String(Base64.getEncoder().encode(encryptedMessage));
    }

    private byte[] encryptBlock(String message) throws IllegalArgumentException {
        if(message.length() > maxMessageLength)
            throw new IllegalArgumentException("Message is too long");

        BigInteger messageRaw = new BigInteger(message.getBytes());

        BigInteger bigIntegerExponent;
        BigInteger messageDigested;
        BigInteger bigIntegerQ = rsaPublicKey.getE();
        BigInteger modulus = rsaPublicKey.getN();

        bigIntegerExponent = messageRaw;
        messageDigested = BigInteger.ONE;

        BigInteger bigIntegerTwo = new BigInteger("2");
        for(; bigIntegerQ.compareTo(BigInteger.ZERO) > 0; bigIntegerQ = bigIntegerQ.divide(bigIntegerTwo)) {
            if(bigIntegerQ.mod(bigIntegerTwo).equals(BigInteger.ONE))
                messageDigested = messageDigested.multiply(bigIntegerExponent).mod(modulus);
            bigIntegerExponent = bigIntegerExponent.multiply(bigIntegerExponent).mod(modulus);
        }
        byte[] encoded = new byte[maxMessageLength];
        //System.out.println(messageDigested.toByteArray().length);
        System.arraycopy(messageDigested.toByteArray(), 0, encoded, maxMessageLength - messageDigested.toByteArray().length, messageDigested.toByteArray().length);
        return encoded;
    }

    private byte[] joinBlocks(byte[] block1, byte[] block2) {
        byte[] joinedBlocks = new byte[block1.length + block2.length];
        System.arraycopy(block1,0, joinedBlocks,0, block1.length);
        System.arraycopy(block2,0, joinedBlocks, block1.length, block2.length);
        return joinedBlocks;
    }
}
