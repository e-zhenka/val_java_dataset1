public void check(String hostname, List<Certificate> peerCertificates)
      throws SSLPeerUnverifiedException {
    Set<ByteString> pins = findMatchingPins(hostname);

    if (pins == null) return;

    if (trustRootIndex != null) {
      peerCertificates = new CertificateChainCleaner(trustRootIndex).clean(peerCertificates);
    }

    for (int i = 0, size = peerCertificates.size(); i < size; i++) {
      X509Certificate x509Certificate = (X509Certificate) peerCertificates.get(i);
      if (pins.contains(sha1(x509Certificate))) return; // Success!
    }

    // If we couldn't find a matching pin, format a nice exception.
    StringBuilder message = new StringBuilder()
        .append("Certificate pinning failure!")
        .append("\n  Peer certificate chain:");
    for (int i = 0, size = peerCertificates.size(); i < size; i++) {
      X509Certificate x509Certificate = (X509Certificate) peerCertificates.get(i);
      message.append("\n    ").append(pin(x509Certificate))
          .append(": ").append(x509Certificate.getSubjectDN().getName());
    }
    message.append("\n  Pinned certificates for ").append(hostname).append(":");
    for (ByteString pin : pins) {
      message.append("\n    sha1/").append(pin.base64());
    }
    throw new SSLPeerUnverifiedException(message.toString());
  }