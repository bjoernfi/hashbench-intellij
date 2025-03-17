package hashbench.benchmark.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class JRESHA256 implements Implementation {
    private final MessageDigest digest;

    public JRESHA256() {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] hash(byte[] data) {
        return digest.digest(data);
    }
}
