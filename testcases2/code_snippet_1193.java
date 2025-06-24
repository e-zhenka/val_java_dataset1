private T buildFromConnection(Http2Connection connection) {
        Long maxHeaderListSize = initialSettings.maxHeaderListSize();
        Http2FrameReader reader = new DefaultHttp2FrameReader(new DefaultHttp2HeadersDecoder(isValidateHeaders(),
                maxHeaderListSize == null ? DEFAULT_HEADER_LIST_SIZE : maxHeaderListSize,
                /* initialHuffmanDecodeCapacity= */ -1));
        Http2FrameWriter writer = encoderIgnoreMaxHeaderListSize == null ?
                new DefaultHttp2FrameWriter(headerSensitivityDetector()) :
                new DefaultHttp2FrameWriter(headerSensitivityDetector(), encoderIgnoreMaxHeaderListSize);

        if (frameLogger != null) {
            reader = new Http2InboundFrameLogger(reader, frameLogger);
            writer = new Http2OutboundFrameLogger(writer, frameLogger);
        }

        Http2ConnectionEncoder encoder = new DefaultHttp2ConnectionEncoder(connection, writer);
        boolean encoderEnforceMaxConcurrentStreams = encoderEnforceMaxConcurrentStreams();

        if (maxQueuedControlFrames != 0) {
            encoder = new Http2ControlFrameLimitEncoder(encoder, maxQueuedControlFrames);
        }
        if (encoderEnforceMaxConcurrentStreams) {
            if (connection.isServer()) {
                encoder.close();
                reader.close();
                throw new IllegalArgumentException(
                        "encoderEnforceMaxConcurrentStreams: " + encoderEnforceMaxConcurrentStreams +
                        " not supported for server");
            }
            encoder = new StreamBufferingEncoder(encoder);
        }

        DefaultHttp2ConnectionDecoder decoder = new DefaultHttp2ConnectionDecoder(connection, encoder, reader,
                promisedRequestVerifier(), isAutoAckSettingsFrame(), isAutoAckPingFrame());
        return buildFromCodec(decoder, encoder);
    }