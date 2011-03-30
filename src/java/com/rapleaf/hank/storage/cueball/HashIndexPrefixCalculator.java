package com.rapleaf.hank.storage.cueball;

public class HashIndexPrefixCalculator {
  private final int numBits;

  public HashIndexPrefixCalculator(int numBits) {
    this.numBits = numBits;
  }

  public int getHashPrefix(final byte[] chunkBytes, int off) {
    int lim = off + (numBits / 8);
    int prefix = 0;
    for (; off < lim; off++) {
      prefix = (prefix << 8) | (chunkBytes[off] & 0xff);
    }
    final int bitsFromLastByte = numBits % 8;
    return (prefix << bitsFromLastByte) | (((chunkBytes[off] & 0xff) >> (8-bitsFromLastByte)));
  }
}
