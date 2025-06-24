@Override
    public HeaderEmitter headersStart(int streamId, boolean headersEndStream)
            throws Http2Exception, IOException {

        // Check the pause state before processing headers since the pause state
        // determines if a new stream is created or if this stream is ignored.
        checkPauseState();

        if (connectionState.get().isNewStreamAllowed()) {
            Stream stream = getStream(streamId, false);
            if (stream == null) {
                stream = createRemoteStream(streamId);
            }
            if (streamId < maxActiveRemoteStreamId) {
                throw new ConnectionException(sm.getString("upgradeHandler.stream.old",
                        Integer.valueOf(streamId), Integer.valueOf(maxActiveRemoteStreamId)),
                        Http2Error.PROTOCOL_ERROR);
            }
            stream.checkState(FrameType.HEADERS);
            stream.receivedStartOfHeaders(headersEndStream);
            closeIdleStreams(streamId);
            return stream;
        } else {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("upgradeHandler.noNewStreams",
                        connectionId, Integer.toString(streamId)));
            }
            reduceOverheadCount();
            // Stateless so a static can be used to save on GC
            return HEADER_SINK;
        }
    }