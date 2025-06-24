public String verifyAndExtract(String signedStr) {
    int index = signedStr.lastIndexOf(SIGNATURE);
    if (index == -1) {
      throw new IllegalArgumentException("Invalid input sign: " + signedStr);
    }
    String originalSignature = signedStr.substring(index + SIGNATURE.length());
    String rawValue = signedStr.substring(0, index);
    String currentSignature = getSignature(rawValue);

    if (LOG.isDebugEnabled()) {
      LOG.debug("Signature generated for " + rawValue + " inside verify is " + currentSignature);
    }
    if (!MessageDigest.isEqual(originalSignature.getBytes(), currentSignature.getBytes())) {
      throw new IllegalArgumentException("Invalid sign, original = " + originalSignature +
        " current = " + currentSignature);
    }
    return rawValue;
  }