private CoderResult decodeHasArray(ByteBuffer in, CharBuffer out) {
        int outRemaining = out.remaining();
        int pos = in.position();
        int limit = in.limit();
        final byte[] bArr = in.array();
        final char[] cArr = out.array();
        final int inIndexLimit = limit + in.arrayOffset();
        int inIndex = pos + in.arrayOffset();
        int outIndex = out.position() + out.arrayOffset();
        // if someone would change the limit in process,
        // he would face consequences
        for (; inIndex < inIndexLimit && outRemaining > 0; inIndex++) {
            int jchar = bArr[inIndex];
            if (jchar < 0) {
                jchar = jchar & 0x7F;
                int tail = remainingBytes[jchar];
                if (tail == -1) {
                    in.position(inIndex - in.arrayOffset());
                    out.position(outIndex - out.arrayOffset());
                    return CoderResult.malformedForLength(1);
                }
                if (inIndexLimit - inIndex < 1 + tail) {
                    // Apache Tomcat added test - detects invalid sequence as
                    // early as possible
                    if (jchar == 0x74 && inIndexLimit > inIndex + 1) {
                        if ((bArr[inIndex + 1] & 0xFF) > 0x8F) {
                            return CoderResult.unmappableForLength(4);
                        }
                    }
                    break;
                }
                for (int i = 0; i < tail; i++) {
                    int nextByte = bArr[inIndex + i + 1] & 0xFF;
                    if ((nextByte & 0xC0) != 0x80) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1 + i);
                    }
                    jchar = (jchar << 6) + nextByte;
                }
                jchar -= remainingNumbers[tail];
                if (jchar < lowerEncodingLimit[tail]) {
                    // Should have been encoded in fewer octets
                    in.position(inIndex - in.arrayOffset());
                    out.position(outIndex - out.arrayOffset());
                    return CoderResult.malformedForLength(1);
                }
                inIndex += tail;
            }
            // Apache Tomcat added test
            if (jchar >= 0xD800 && jchar <= 0xDFFF) {
                return CoderResult.unmappableForLength(3);
            }
            // Apache Tomcat added test
            if (jchar > 0x10FFFF) {
                return CoderResult.unmappableForLength(4);
            }
            if (jchar <= 0xffff) {
                cArr[outIndex++] = (char) jchar;
                outRemaining--;
            } else {
                if (outRemaining < 2) {
                    return CoderResult.OVERFLOW;
                }
                cArr[outIndex++] = (char) ((jchar >> 0xA) + 0xD7C0);
                cArr[outIndex++] = (char) ((jchar & 0x3FF) + 0xDC00);
                outRemaining -= 2;
            }
        }
        in.position(inIndex - in.arrayOffset());
        out.position(outIndex - out.arrayOffset());
        return (outRemaining == 0 && inIndex < inIndexLimit) ? CoderResult.OVERFLOW
                : CoderResult.UNDERFLOW;
    }