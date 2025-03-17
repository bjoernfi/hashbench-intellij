package hashbench.benchmark.impl;

import org.bouncycastle.crypto.digests.SHA256Digest;

public class BouncyCastleSHA256 implements Implementation {
    private final SHA256Digest digest;

    public BouncyCastleSHA256() {
        digest = new SHA256Digest();
    }

    @Override
    public byte[] hash(byte[] data) {
        byte[] output = new byte[digest.getDigestSize()];
        digest.update(data, 0, data.length);
        digest.doFinal(output, 0);
        return output;
    }
}
