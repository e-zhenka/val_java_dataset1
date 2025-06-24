private boolean processRemainingHeader() throws IOException {
        // Ignore the 2 bytes already read. 4 for the mask
        int headerLength;
        if (isMasked()) {
            headerLength = 4;
        } else {
            headerLength = 0;
        }
        // Add additional bytes depending on length
        if (payloadLength == 126) {
            headerLength += 2;
        } else if (payloadLength == 127) {
            headerLength += 8;
        }
        if (writePos - readPos < headerLength) {
            return false;
        }
        // Calculate new payload length if necessary
        if (payloadLength == 126) {
            payloadLength = byteArrayToLong(inputBuffer, readPos, 2);
            readPos += 2;
        } else if (payloadLength == 127) {
            payloadLength = byteArrayToLong(inputBuffer, readPos, 8);
            // The most significant bit of those 8 bytes is required to be zero
            // (see RFC 6455, section 5.2). If the most significant bit is set,
            // the resulting payload length will be negative so test for that.
            if (payloadLength < 0) {
                throw new WsIOException(
                        new CloseReason(CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.payloadMsbInvalid")));
            }
            readPos += 8;
        }
        if (Util.isControl(opCode)) {
            if (payloadLength > 125) {
                throw new WsIOException(new CloseReason(
                        CloseCodes.PROTOCOL_ERROR,
                        sm.getString("wsFrame.controlPayloadTooBig",
                                Long.valueOf(payloadLength))));
            }
            if (!fin) {
                throw new WsIOException(new CloseReason(
                        CloseCodes.PROTOCOL_ERROR,
                        sm.getString("wsFrame.controlNoFin")));
            }
        }
        if (isMasked()) {
            System.arraycopy(inputBuffer, readPos, mask, 0, 4);
            readPos += 4;
        }
        state = State.DATA;
        return true;
    }