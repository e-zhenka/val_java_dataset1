@Override
    public synchronized SSLEngineResult wrap(
            final ByteBuffer[] srcs, final int offset, final int length, final ByteBuffer dst) throws SSLException {

        // Check to make sure the engine has not been closed
        if (isDestroyed()) {
            return CLOSED_NOT_HANDSHAKING;
        }

        // Throw required runtime exceptions
        if (srcs == null) {
            throw new IllegalArgumentException("srcs is null");
        }
        if (dst == null) {
            throw new IllegalArgumentException("dst is null");
        }

        if (offset >= srcs.length || offset + length > srcs.length) {
            throw new IndexOutOfBoundsException(
                    "offset: " + offset + ", length: " + length +
                            " (expected: offset <= offset + length <= srcs.length (" + srcs.length + "))");
        }

        if (dst.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }

        HandshakeStatus status = NOT_HANDSHAKING;
        // Prepare OpenSSL to work in server mode and receive handshake
        if (handshakeState != HandshakeState.FINISHED) {
            if (handshakeState != HandshakeState.STARTED_EXPLICITLY) {
                // Update accepted so we know we triggered the handshake via wrap
                handshakeState = HandshakeState.STARTED_IMPLICITLY;
            }

            status = handshake();
            if (status == NEED_UNWRAP) {
                return NEED_UNWRAP_OK;
            }

            if (engineClosed) {
                return NEED_UNWRAP_CLOSED;
            }
        }

        // There was no pending data in the network BIO -- encrypt any application data
        int bytesProduced = 0;
        int bytesConsumed = 0;
        int endOffset = offset + length;
        for (int i = offset; i < endOffset; ++ i) {
            final ByteBuffer src = srcs[i];
            if (src == null) {
                throw new IllegalArgumentException("srcs[" + i + "] is null");
            }
            while (src.hasRemaining()) {
                final SSLEngineResult pendingNetResult;
                // Write plaintext application data to the SSL engine
                int result = writePlaintextData(src);
                if (result > 0) {
                    bytesConsumed += result;

                    pendingNetResult = readPendingBytesFromBIO(dst, bytesConsumed, bytesProduced, status);
                    if (pendingNetResult != null) {
                        return pendingNetResult;
                    }
                } else {
                    int sslError = SSL.getError(ssl, result);
                    switch (sslError) {
                    case SSL.SSL_ERROR_ZERO_RETURN:
                        // This means the connection was shutdown correctly, close inbound and outbound
                        if (!receivedShutdown) {
                            closeAll();
                        }
                        pendingNetResult = readPendingBytesFromBIO(dst, bytesConsumed, bytesProduced, status);
                        return pendingNetResult != null ? pendingNetResult : CLOSED_NOT_HANDSHAKING;
                    case SSL.SSL_ERROR_WANT_READ:
                        // If there is no pending data to read from BIO we should go back to event loop and try to read
                        // more data [1]. It is also possible that event loop will detect the socket has been closed.
                        // [1] https://www.openssl.org/docs/manmaster/ssl/SSL_write.html
                        pendingNetResult = readPendingBytesFromBIO(dst, bytesConsumed, bytesProduced, status);
                        return pendingNetResult != null ? pendingNetResult :
                                new SSLEngineResult(getEngineStatus(), NEED_UNWRAP, bytesConsumed, bytesProduced);
                    case SSL.SSL_ERROR_WANT_WRITE:
                        // SSL_ERROR_WANT_WRITE typically means that the underlying transport is not writable and we
                        // should set the "want write" flag on the selector and try again when the underlying transport
                        // is writable [1]. However we are not directly writing to the underlying transport and instead
                        // writing to a BIO buffer. The OpenSsl documentation says we should do the following [1]:
                        //
                        // "When using a buffering BIO, like a BIO pair, data must be written into or retrieved out of
                        // the BIO before being able to continue."
                        //
                        // So we attempt to drain the BIO buffer below, but if there is no data this condition is
                        // undefined and we assume their is a fatal error with the openssl engine and close.
                        // [1] https://www.openssl.org/docs/manmaster/ssl/SSL_write.html
                        pendingNetResult = readPendingBytesFromBIO(dst, bytesConsumed, bytesProduced, status);
                        return pendingNetResult != null ? pendingNetResult : NEED_WRAP_CLOSED;
                    default:
                        // Everything else is considered as error
                        throw shutdownWithError("SSL_write");
                    }
                }
            }
        }
        // We need to check if pendingWrittenBytesInBIO was checked yet, as we may not checked if the srcs was empty,
        // or only contained empty buffers.
        if (bytesConsumed == 0) {
            SSLEngineResult pendingNetResult = readPendingBytesFromBIO(dst, 0, bytesProduced, status);
            if (pendingNetResult != null) {
                return pendingNetResult;
            }
        }

        return newResult(bytesConsumed, bytesProduced, status);
    }