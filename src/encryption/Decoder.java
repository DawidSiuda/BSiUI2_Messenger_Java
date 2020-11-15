package encryption;

import model.RSAPrivateKey;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Base64;

public class Decoder {
    private final RSAPrivateKey rsaPrivateKey;
    private int maxMessageLength;

    public Decoder(RSAPrivateKey rsaPrivateKey) {
        this.rsaPrivateKey = rsaPrivateKey;
        this.maxMessageLength = rsaPrivateKey.getN().toByteArray().length;
        if(maxMessageLength % 2 == 1)
            this.maxMessageLength -= 1;
    }

    public String decodeMessage(String encryptedMessageBase64) {
        byte[] rawEncodedMessage = Base64.getDecoder().decode(encryptedMessageBase64);
        StringBuilder decodedMessageBuilder = new StringBuilder("");
        for(int blockStart = 0; blockStart < rawEncodedMessage.length; blockStart += maxMessageLength) {
            byte[] block =  Arrays.copyOfRange(rawEncodedMessage, blockStart, blockStart + maxMessageLength);
            decodedMessageBuilder.append(decodeBlock(block));
        }
        return decodedMessageBuilder.toString();
    }

    private String decodeBlock(byte[] block) {
        BigInteger pot,wyn,bigIntegerQ;

        BigInteger modulus = rsaPrivateKey.getN();
        bigIntegerQ = rsaPrivateKey.getD();
        pot = new BigInteger(block);
        wyn = BigInteger.ONE;
        BigInteger bigIntegerTwo = new BigInteger("2");
        for(; bigIntegerQ.compareTo(BigInteger.ZERO) > 0; bigIntegerQ = bigIntegerQ.divide(bigIntegerTwo))
        {
            if(bigIntegerQ.mod(bigIntegerTwo).equals(BigInteger.ONE))
                wyn = wyn.multiply(pot).mod(modulus);
            pot = pot.multiply(pot).mod(modulus);
        }
        return bigIntegerToString(wyn);
    }

    private String bigIntegerToString(BigInteger bigInteger) {
        return new String(bigInteger.toByteArray());
    }
}
