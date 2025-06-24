static protected UTF8Buffer readUTF(DataByteArrayInputStream is) throws ProtocolException {
        int size = is.readUnsignedShort();
        Buffer buffer = is.readBuffer(size);
        if (buffer == null || buffer.length != size) {
            throw new ProtocolException("Invalid message encoding");
        }
        return buffer.utf8();
    }