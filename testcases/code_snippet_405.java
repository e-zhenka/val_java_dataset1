private int readEncryptedData(final ByteBuffer dst, final int pending) throws SSLException {
        try {
            int bytesRead = 0;
            final int pos = dst.position();
            if (dst.remaining() >= pending) {
                final int limit = dst.limit();
                final int len = min(pending, limit - pos);
                if (dst.isDirect()) {
                    bytesRead = readEncryptedDataDirect(dst, pos, len);
                    // Need to update the position on the dst buffer.
                    if (bytesRead > 0) {
                        dst.position(pos + bytesRead);
                    }
                } else {
                    // The heap method will update the position on the dst buffer automatically.
                    bytesRead = readEncryptedDataHeap(dst, len);
                }
            }

            return bytesRead;
        } catch (Exception e) {
            throw convertException(e);
        }
    }