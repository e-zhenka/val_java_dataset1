private static void assertSendsCorrectly(final byte[] sourceBytes, SSLEngine source,
            SSLEngine dest, boolean needsRecordSplit) throws SSLException {
        ByteBuffer sourceOut = ByteBuffer.wrap(sourceBytes);
        SSLSession sourceSession = source.getSession();
        ByteBuffer sourceToDest = ByteBuffer.allocate(sourceSession.getPacketBufferSize());
        SSLEngineResult sourceOutRes = source.wrap(sourceOut, sourceToDest);
        sourceToDest.flip();

        String sourceCipherSuite = source.getSession().getCipherSuite();
        assertEquals(sourceCipherSuite, sourceBytes.length, sourceOutRes.bytesConsumed());
        assertEquals(sourceCipherSuite, HandshakeStatus.NOT_HANDSHAKING,
                sourceOutRes.getHandshakeStatus());

        SSLSession destSession = dest.getSession();
        ByteBuffer destIn = ByteBuffer.allocate(destSession.getApplicationBufferSize());

        int numUnwrapCalls = 0;
        while (destIn.position() != sourceOut.limit()) {
            SSLEngineResult destRes = dest.unwrap(sourceToDest, destIn);
            assertEquals(sourceCipherSuite, HandshakeStatus.NOT_HANDSHAKING,
                    destRes.getHandshakeStatus());
            if (needsRecordSplit && numUnwrapCalls == 0) {
                assertEquals(sourceCipherSuite, 1, destRes.bytesProduced());
            }
            numUnwrapCalls++;
        }

        destIn.flip();
        byte[] actual = new byte[destIn.remaining()];
        destIn.get(actual);
        assertEquals(sourceCipherSuite, Arrays.toString(sourceBytes), Arrays.toString(actual));

        if (needsRecordSplit) {
            assertEquals(sourceCipherSuite, 2, numUnwrapCalls);
        } else {
            assertEquals(sourceCipherSuite, 1, numUnwrapCalls);
        }
    }