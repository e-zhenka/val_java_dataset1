@Override
        public int fill(ByteBuffer buffer) throws IOException
        {
            try
            {
                synchronized (_decryptedEndPoint)
                {
                    if (LOG.isDebugEnabled())
                        LOG.debug(">fill {}", SslConnection.this);

                    int filled = -2;
                    try
                    {
                        if (_fillState != FillState.IDLE)
                            return filled = 0;

                        // Do we already have some decrypted data?
                        if (BufferUtil.hasContent(_decryptedInput))
                            return filled = BufferUtil.append(buffer, _decryptedInput);

                        // loop filling and unwrapping until we have something
                        while (true)
                        {
                            HandshakeStatus status = _sslEngine.getHandshakeStatus();
                            if (LOG.isDebugEnabled())
                                LOG.debug("fill {}", status);
                            switch (status)
                            {
                                case NEED_UNWRAP:
                                case NOT_HANDSHAKING:
                                    break;

                                case NEED_TASK:
                                    _sslEngine.getDelegatedTask().run();
                                    continue;

                                case NEED_WRAP:
                                    if (_flushState == FlushState.IDLE && flush(BufferUtil.EMPTY_BUFFER))
                                    {
                                        Throwable failure = _failure;
                                        if (failure != null)
                                            rethrow(failure);
                                        if (_sslEngine.isInboundDone())
                                            return filled = -1;
                                        continue;
                                    }
                                    // Handle in needsFillInterest().
                                    return filled = 0;

                                default:
                                    throw new IllegalStateException("Unexpected HandshakeStatus " + status);
                            }

                            acquireEncryptedInput();

                            // can we use the passed buffer if it is big enough
                            ByteBuffer appIn;
                            int appBufferSize = getApplicationBufferSize();
                            if (_decryptedInput == null)
                            {
                                if (BufferUtil.space(buffer) > appBufferSize)
                                    appIn = buffer;
                                else
                                    appIn = _decryptedInput = _bufferPool.acquire(appBufferSize, _decryptedDirectBuffers);
                            }
                            else
                            {
                                appIn = _decryptedInput;
                                BufferUtil.compact(_encryptedInput);
                            }

                            // Let's try reading some encrypted data... even if we have some already.
                            int netFilled = networkFill(_encryptedInput);
                            if (LOG.isDebugEnabled())
                                LOG.debug("net filled={}", netFilled);

                            // Workaround for Java 11 behavior.
                            if (netFilled < 0 && isHandshakeInitial() && BufferUtil.isEmpty(_encryptedInput))
                                closeInbound();

                            if (netFilled > 0 && !isHandshakeComplete() && isOutboundDone())
                                throw new SSLHandshakeException("Closed during handshake");

                            if (_handshake.compareAndSet(HandshakeState.INITIAL, HandshakeState.HANDSHAKE))
                            {
                                if (LOG.isDebugEnabled())
                                    LOG.debug("fill starting handshake {}", SslConnection.this);
                            }

                            // Let's unwrap even if we have no net data because in that
                            // case we want to fall through to the handshake handling
                            int pos = BufferUtil.flipToFill(appIn);
                            SSLEngineResult unwrapResult;
                            try
                            {
                                _underflown = false;
                                unwrapResult = unwrap(_sslEngine, _encryptedInput, appIn);
                            }
                            finally
                            {
                                BufferUtil.flipToFlush(appIn, pos);
                            }
                            if (LOG.isDebugEnabled())
                                LOG.debug("unwrap net_filled={} {} encryptedBuffer={} unwrapBuffer={} appBuffer={}",
                                    netFilled,
                                    StringUtil.replace(unwrapResult.toString(), '\n', ' '),
                                    BufferUtil.toSummaryString(_encryptedInput),
                                    BufferUtil.toDetailString(appIn),
                                    BufferUtil.toDetailString(buffer));

                            SSLEngineResult.Status unwrap = unwrapResult.getStatus();

                            // Extra check on unwrapResultStatus == OK with zero bytes consumed
                            // or produced is due to an SSL client on Android (see bug #454773).
                            if (unwrap == Status.OK && unwrapResult.bytesConsumed() == 0 && unwrapResult.bytesProduced() == 0)
                                unwrap = Status.BUFFER_UNDERFLOW;

                            switch (unwrap)
                            {
                                case CLOSED:
                                    Throwable failure = _failure;
                                    if (failure != null)
                                        rethrow(failure);
                                    return filled = -1;

                                case BUFFER_UNDERFLOW:
                                    if (BufferUtil.space(_encryptedInput) == 0)
                                    {
                                        BufferUtil.clear(_encryptedInput);
                                        throw new SSLHandshakeException("Encrypted buffer max length exceeded");
                                    }

                                    if (netFilled > 0)
                                        continue; // try filling some more

                                    _underflown = true;
                                    if (netFilled < 0 && _sslEngine.getUseClientMode())
                                    {
                                        Throwable closeFailure = closeInbound();
                                        if (_flushState == FlushState.WAIT_FOR_FILL)
                                        {
                                            Throwable handshakeFailure = new SSLHandshakeException("Abruptly closed by peer");
                                            if (closeFailure != null)
                                                handshakeFailure.addSuppressed(closeFailure);
                                            throw handshakeFailure;
                                        }
                                        return filled = -1;
                                    }
                                    return filled = netFilled;

                                case BUFFER_OVERFLOW:
                                    // It's possible that SSLSession.applicationBufferSize has been expanded
                                    // by the SSLEngine implementation. Unwrapping a large encrypted buffer
                                    // causes BUFFER_OVERFLOW because the (old) applicationBufferSize is
                                    // too small. Release the decrypted input buffer so it will be re-acquired
                                    // with the larger capacity.
                                    // See also system property "jsse.SSLEngine.acceptLargeFragments".
                                    if (BufferUtil.isEmpty(_decryptedInput) && appBufferSize < getApplicationBufferSize())
                                    {
                                        releaseDecryptedInputBuffer();
                                        continue;
                                    }
                                    throw new IllegalStateException("Unexpected unwrap result " + unwrap);

                                case OK:
                                    if (unwrapResult.getHandshakeStatus() == HandshakeStatus.FINISHED)
                                        handshakeSucceeded();

                                    if (isRenegotiating() && !allowRenegotiate())
                                        return filled = -1;

                                    // If bytes were produced, don't bother with the handshake status;
                                    // pass the decrypted data to the application, which will perform
                                    // another call to fill() or flush().
                                    if (unwrapResult.bytesProduced() > 0)
                                    {
                                        if (appIn == buffer)
                                            return filled = unwrapResult.bytesProduced();
                                        return filled = BufferUtil.append(buffer, _decryptedInput);
                                    }

                                    break;

                                default:
                                    throw new IllegalStateException("Unexpected unwrap result " + unwrap);
                            }
                        }
                    }
                    catch (Throwable x)
                    {
                        Throwable f = handleException(x, "fill");
                        Throwable failure = handshakeFailed(f);
                        if (_flushState == FlushState.WAIT_FOR_FILL)
                        {
                            _flushState = FlushState.IDLE;
                            getExecutor().execute(() -> _decryptedEndPoint.getWriteFlusher().onFail(failure));
                        }
                        throw failure;
                    }
                    finally
                    {
                        releaseEncryptedInputBuffer();
                        releaseDecryptedInputBuffer();

                        if (_flushState == FlushState.WAIT_FOR_FILL)
                        {
                            _flushState = FlushState.IDLE;
                            getExecutor().execute(() -> _decryptedEndPoint.getWriteFlusher().completeWrite());
                        }

                        if (LOG.isDebugEnabled())
                            LOG.debug("<fill f={} uf={} {}", filled, _underflown, SslConnection.this);
                    }
                }
            }
            catch (Throwable x)
            {
                close(x);
                rethrow(x);
                // Never reached.
                throw new AssertionError();
            }
        }