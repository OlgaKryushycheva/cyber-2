package org.example;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Provides RSA key generation, encryption, and decryption utilities.
 */
public class RSAService {
    private static final SecureRandom RANDOM = new SecureRandom();

    public RSAKeyPair generateKeyPair(int bitLength) {
        if (bitLength < 512) {
            throw new IllegalArgumentException("Key length should be at least 512 bits for this demo");
        }

        int primeSize = bitLength / 2;
        BigInteger p = BigInteger.probablePrime(primeSize, RANDOM);
        BigInteger q = BigInteger.probablePrime(primeSize, RANDOM);
        while (p.equals(q)) {
            q = BigInteger.probablePrime(primeSize, RANDOM);
        }

        BigInteger n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        BigInteger e = BigInteger.valueOf(65537);
        if (!phi.gcd(e).equals(BigInteger.ONE)) {
            e = findCoprimeExponent(phi);
        }

        BigInteger d = e.modInverse(phi);
        return new RSAKeyPair(n, e, d, phi);
    }

    private BigInteger findCoprimeExponent(BigInteger phi) {
        BigInteger candidate = BigInteger.valueOf(3);
        while (candidate.compareTo(phi) < 0) {
            if (phi.gcd(candidate).equals(BigInteger.ONE)) {
                return candidate;
            }
            candidate = candidate.add(BigInteger.TWO);
        }
        throw new IllegalStateException("Unable to find coprime exponent");
    }

    public String encrypt(String plainText, RSAKeyPair keyPair) {
        if (plainText.isEmpty()) {
            throw new IllegalArgumentException("Input text is empty");
        }
        BigInteger message = new BigInteger(1, plainText.getBytes(StandardCharsets.UTF_8));
        if (message.compareTo(keyPair.modulus()) >= 0) {
            throw new IllegalArgumentException("Message is too long for the selected key size");
        }
        BigInteger cipher = message.modPow(keyPair.publicExponent(), keyPair.modulus());
        return Base64.getEncoder().encodeToString(cipher.toByteArray());
    }

    public String decrypt(String cipherText, RSAKeyPair keyPair) {
        byte[] cipherBytes = Base64.getDecoder().decode(cipherText);
        BigInteger cipher = new BigInteger(1, cipherBytes);
        BigInteger message = cipher.modPow(keyPair.privateExponent(), keyPair.modulus());
        return new String(message.toByteArray(), StandardCharsets.UTF_8);
    }
}
