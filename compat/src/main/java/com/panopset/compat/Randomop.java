package com.panopset.compat;

import java.security.SecureRandom;

public enum Randomop {

  INSTANCE;

  private final SecureRandom secureRandom = new SecureRandom();

  public static int random(int min, int max) {
    if (min == max) {
      return min;
    } else {
      int shift = min;
      if (min > max) {
        shift = max;
      }
      return INSTANCE.secureRandom.nextInt(Math.abs(max - min) + 1) + shift;
    }
  }

  public static void nextBytes(byte[] bytes) {
    INSTANCE.secureRandom.nextBytes(bytes);
  }

  public static int nextInt() {
    return INSTANCE.secureRandom.nextInt();
  }

  public static long nextLong() {
    return INSTANCE.secureRandom.nextLong();
  }
}
