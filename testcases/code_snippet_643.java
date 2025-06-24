public AsciiString generateSessionId() {
    return AsciiString.cached(UUID.randomUUID().toString());
  }