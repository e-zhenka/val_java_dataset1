private static void writeString(ByteBuffer buffer, String string) {
        int length = string.length();
        for (int charIndex = 0; charIndex < length; charIndex++) {
            char c = string.charAt(charIndex);
            if(c != '\r' && c != '\n') {
                buffer.put((byte) c);
            } else {
                buffer.put((byte) ' ');
            }
        }
    }