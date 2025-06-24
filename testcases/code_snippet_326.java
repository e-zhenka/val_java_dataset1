public AsciiString generateSessionId() {
    ThreadLocalRandom random = ThreadLocalRandom.current();
    UUID uuid = new UUID(random.nextLong(), random.nextLong());
    return AsciiString.of(uuid.toString());
  }