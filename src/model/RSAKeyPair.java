package model;

import java.math.BigInteger;

public class RSAKeyPair {
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    public RSAKeyPair(BigInteger d, BigInteger e, BigInteger n) {
        this.privateKey = new RSAPrivateKey(d, n);
        this.publicKey = new RSAPublicKey(e, n);
    }

    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }
}
