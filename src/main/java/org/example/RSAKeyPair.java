package org.example;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Represents an RSA key pair with modulus n and exponents e (public) and d (private).
 */
public class RSAKeyPair {
    private final BigInteger modulus;
    private final BigInteger publicExponent;
    private final BigInteger privateExponent;
    private final BigInteger phi;

    public RSAKeyPair(BigInteger modulus, BigInteger publicExponent, BigInteger privateExponent, BigInteger phi) {
        this.modulus = modulus;
        this.publicExponent = publicExponent;
        this.privateExponent = privateExponent;
        this.phi = phi;
    }

    public BigInteger modulus() {
        return modulus;
    }

    public BigInteger publicExponent() {
        return publicExponent;
    }

    public BigInteger privateExponent() {
        return privateExponent;
    }

    public BigInteger phi() {
        return phi;
    }

    @Override
    public String toString() {
        return "n=" + modulus + ", e=" + publicExponent + ", d=" + privateExponent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RSAKeyPair that = (RSAKeyPair) o;
        return modulus.equals(that.modulus) && publicExponent.equals(that.publicExponent) && privateExponent.equals(that.privateExponent) && phi.equals(that.phi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modulus, publicExponent, privateExponent, phi);
    }
}
