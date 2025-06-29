@Override
    public SessionData decode(String data) {
        byte[] bytes = Base64.getDecoder().decode(data);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             WhitelistObjectInputStream objectInputStream = new WhitelistObjectInputStream(inputStream)) {
            return (SessionData) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new PippoRuntimeException(e, "Cannot deserialize session. A new one will be created.");
        }
    }