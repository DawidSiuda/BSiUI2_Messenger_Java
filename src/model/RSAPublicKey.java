package model;

import java.math.BigInteger;

public class RSAPublicKey {
    private final BigInteger e;
    private final BigInteger n;

    public RSAPublicKey(BigInteger e, BigInteger n) {
        this.e = e;
        this.n = n;
    }

    public BigInteger getE() {
        return e;
    }

    public BigInteger getN() {
        return n;
    }
}