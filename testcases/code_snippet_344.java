@Override
        public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency,
                short weight, boolean exclusive, int padding, boolean endOfStream) throws Http2Exception {
            Http2Stream stream = connection.stream(streamId);
            boolean allowHalfClosedRemote = false;
            if (stream == null && !connection.streamMayHaveExisted(streamId)) {
                stream = connection.remote().createStream(streamId, endOfStream);
                // Allow the state to be HALF_CLOSE_REMOTE if we're creating it in that state.
                allowHalfClosedRemote = stream.state() == HALF_CLOSED_REMOTE;
            }

            if (shouldIgnoreHeadersOrDataFrame(ctx, streamId, stream, "HEADERS")) {
                return;
            }

            boolean isInformational = !connection.isServer() &&
                    HttpStatusClass.valueOf(headers.status()) == INFORMATIONAL;
            if ((isInformational || !endOfStream) && stream.isHeadersReceived() || stream.isTrailersReceived()) {
                throw streamError(streamId, PROTOCOL_ERROR,
                                  "Stream %d received too many headers EOS: %s state: %s",
                                  streamId, endOfStream, stream.state());
            }

            switch (stream.state()) {
                case RESERVED_REMOTE:
                    stream.open(endOfStream);
                    break;
                case OPEN:
                case HALF_CLOSED_LOCAL:
                    // Allowed to receive headers in these states.
                    break;
                case HALF_CLOSED_REMOTE:
                    if (!allowHalfClosedRemote) {
                        throw streamError(stream.id(), STREAM_CLOSED, "Stream %d in unexpected state: %s",
                                stream.id(), stream.state());
                    }
                    break;
                case CLOSED:
                    throw streamError(stream.id(), STREAM_CLOSED, "Stream %d in unexpected state: %s",
                            stream.id(), stream.state());
                default:
                    // Connection error.
                    throw connectionError(PROTOCOL_ERROR, "Stream %d in unexpected state: %s", stream.id(),
                            stream.state());
            }

            if (!stream.isHeadersReceived()) {
                // extract the content-length header
                List<? extends CharSequence> contentLength = headers.getAll(HttpHeaderNames.CONTENT_LENGTH);
                if (contentLength != null && !contentLength.isEmpty()) {
                    try {
                        long cLength = HttpUtil.normalizeAndGetContentLength(contentLength, false, true);
                        if (cLength != -1) {
                            headers.setLong(HttpHeaderNames.CONTENT_LENGTH, cLength);
                            stream.setProperty(contentLengthKey, new ContentLength(cLength));
                        }
                    } catch (IllegalArgumentException e) {
                        throw streamError(stream.id(), PROTOCOL_ERROR, e,
                                "Multiple content-length headers received");
                    }
                }
            }

            stream.headersReceived(isInformational);
            verifyContentLength(stream, 0, endOfStream);
            encoder.flowController().updateDependencyTree(streamId, streamDependency, weight, exclusive);
            listener.onHeadersRead(ctx, streamId, headers, streamDependency,
                    weight, exclusive, padding, endOfStream);
            // If the headers completes this stream, close it.
            if (endOfStream) {
                lifecycleManager.closeStreamRemote(stream, ctx.newSucceededFuture());
            }
        }