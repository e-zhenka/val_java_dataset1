private State readHeaders(ByteBuf buffer) {
        final HttpMessage message = this.message;
        final HttpHeaders headers = message.headers();

        AppendableCharSequence line = headerParser.parse(buffer);
        if (line == null) {
            return null;
        }
        if (line.length() > 0) {
            do {
                char firstChar = line.charAtUnsafe(0);
                if (name != null && (firstChar == ' ' || firstChar == '\t')) {
                    //please do not make one line from below code
                    //as it breaks +XX:OptimizeStringConcat optimization
                    String trimmedLine = line.toString().trim();
                    String valueStr = String.valueOf(value);
                    value = valueStr + ' ' + trimmedLine;
                } else {
                    if (name != null) {
                        headers.add(name, value);
                    }
                    splitHeader(line);
                }

                line = headerParser.parse(buffer);
                if (line == null) {
                    return null;
                }
            } while (line.length() > 0);
        }

        // Add the last header.
        if (name != null) {
            headers.add(name, value);
        }

        // reset name and value fields
        name = null;
        value = null;

        List<String> contentLengthFields = headers.getAll(HttpHeaderNames.CONTENT_LENGTH);
        if (!contentLengthFields.isEmpty()) {
            HttpVersion version = message.protocolVersion();
            boolean isHttp10OrEarlier = version.majorVersion() < 1 || (version.majorVersion() == 1
                    && version.minorVersion() == 0);
            // Guard against multiple Content-Length headers as stated in
            // https://tools.ietf.org/html/rfc7230#section-3.3.2:
            contentLength = HttpUtil.normalizeAndGetContentLength(contentLengthFields,
                    isHttp10OrEarlier, allowDuplicateContentLengths);
            if (contentLength != -1) {
                headers.set(HttpHeaderNames.CONTENT_LENGTH, contentLength);
            }
        }

        if (isContentAlwaysEmpty(message)) {
            HttpUtil.setTransferEncodingChunked(message, false);
            return State.SKIP_CONTROL_CHARS;
        } else if (HttpUtil.isTransferEncodingChunked(message)) {
            if (!contentLengthFields.isEmpty() && message.protocolVersion() == HttpVersion.HTTP_1_1) {
                handleTransferEncodingChunkedWithContentLength(message);
            }
            return State.READ_CHUNK_SIZE;
        } else if (contentLength() >= 0) {
            return State.READ_FIXED_LENGTH_CONTENT;
        } else {
            return State.READ_VARIABLE_LENGTH_CONTENT;
        }
    }