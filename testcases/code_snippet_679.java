static protected UTF8Buffer readUTF(DataByteArrayInputStream is) throws ProtocolException {
        int size = is.readUnsignedShort();
        if (size < 0) {
            throw new ProtocolException("Invalid message encoding");
        }
        Buffer buffer = is.readBuffer(size);
        if (buffer == null || buffer.length != size) {
            throw new ProtocolException("Invalid message encoding");
        }
        return buffer.utf8();
    }