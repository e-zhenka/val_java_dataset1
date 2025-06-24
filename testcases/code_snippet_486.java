private void processParameters(byte bytes[], int start, int len,
                                  Charset charset) {
        
        if(log.isDebugEnabled()) {
            try {
                log.debug(sm.getString("parameters.bytes",
                        new String(bytes, start, len, DEFAULT_CHARSET.name())));
            } catch (UnsupportedEncodingException uee) {
                // Not possible. All JVMs must support ISO-8859-1
            }
        }

        int decodeFailCount = 0;
            
        int pos = start;
        int end = start + len;

        while(pos < end) {
            parameterCount ++;

            if (limit > -1 && parameterCount >= limit) {
                parseFailed = true;
                log.warn(sm.getString("parameters.maxCountFail",
                        Integer.toString(limit)));
                break;
            }
            int nameStart = pos;
            int nameEnd = -1;
            int valueStart = -1;
            int valueEnd = -1;

            boolean parsingName = true;
            boolean decodeName = false;
            boolean decodeValue = false;
            boolean parameterComplete = false;

            do {
                switch(bytes[pos]) {
                    case '=':
                        if (parsingName) {
                            // Name finished. Value starts from next character
                            nameEnd = pos;
                            parsingName = false;
                            valueStart = ++pos;
                        } else {
                            // Equals character in value
                            pos++;
                        }
                        break;
                    case '&':
                        if (parsingName) {
                            // Name finished. No value.
                            nameEnd = pos;
                        } else {
                            // Value finished
                            valueEnd  = pos;
                        }
                        parameterComplete = true;
                        pos++;
                        break;
                    case '%':
                    case '+':
                        // Decoding required
                        if (parsingName) {
                            decodeName = true;
                        } else {
                            decodeValue = true;
                        }
                        pos ++;
                        break;
                    default:
                        pos ++;
                        break;
                }
            } while (!parameterComplete && pos < end);

            if (pos == end) {
                if (nameEnd == -1) {
                    nameEnd = pos;
                } else if (valueStart > -1 && valueEnd == -1){
                    valueEnd = pos;
                }
            }
            
            if (log.isDebugEnabled() && valueStart == -1) {
                try {
                    log.debug(sm.getString("parameters.noequal",
                            Integer.toString(nameStart),
                            Integer.toString(nameEnd),
                            new String(bytes, nameStart, nameEnd-nameStart,
                                    DEFAULT_CHARSET.name())));
                } catch (UnsupportedEncodingException uee) {
                    // Not possible. All JVMs must support ISO-8859-1
                }
            }
            
            if (nameEnd <= nameStart ) {
                if (log.isInfoEnabled()) {
                    if (valueEnd >= nameStart && log.isDebugEnabled()) {
                        String extract = null;
                        try {
                            extract = new String(bytes, nameStart,
                                    valueEnd - nameStart,
                                    DEFAULT_CHARSET.name());
                        } catch (UnsupportedEncodingException uee) {
                            // Not possible. All JVMs must support ISO-8859-1
                        }
                        log.info(sm.getString("parameters.invalidChunk",
                                Integer.toString(nameStart),
                                Integer.toString(valueEnd),
                                extract));
                    } else {
                        log.info(sm.getString("parameters.invalidChunk",
                                Integer.toString(nameStart),
                                Integer.toString(nameEnd),
                                null));
                    }
                }
                parseFailed = true;
                continue;
                // invalid chunk - it's better to ignore
            }
            
            tmpName.setBytes(bytes, nameStart, nameEnd - nameStart);
            tmpValue.setBytes(bytes, valueStart, valueEnd - valueStart);

            // Take copies as if anything goes wrong originals will be
            // corrupted. This means original values can be logged.
            // For performance - only done for debug
            if (log.isDebugEnabled()) {
                try {
                    origName.append(bytes, nameStart, nameEnd - nameStart);
                    origValue.append(bytes, valueStart, valueEnd - valueStart);
                } catch (IOException ioe) {
                    // Should never happen...
                    log.error(sm.getString("parameters.copyFail"), ioe);
                }
            }
            
            try {
                String name;
                String value;

                if (decodeName) {
                    urlDecode(tmpName);
                }
                tmpName.setCharset(charset);
                name = tmpName.toString();

                if (decodeValue) {
                    urlDecode(tmpValue);
                }
                tmpValue.setCharset(charset);
                value = tmpValue.toString();

                addParam(name, value);
            } catch (IOException e) {
                parseFailed = true;
                decodeFailCount++;
                if (decodeFailCount == 1 || log.isDebugEnabled()) {
                    if (log.isDebugEnabled()) {
                        log.debug(sm.getString("parameters.decodeFail.debug",
                                origName.toString(), origValue.toString()), e);
                    } else if (log.isInfoEnabled()) {
                        log.info(sm.getString("parameters.decodeFail.info",
                                tmpName.toString(), tmpValue.toString()), e);
                    }
                }
            }

            tmpName.recycle();
            tmpValue.recycle();
            // Only recycle copies if we used them
            if (log.isDebugEnabled()) {
                origName.recycle();
                origValue.recycle();
            }
        }

        if (decodeFailCount > 1 && !log.isDebugEnabled()) {
            log.info(sm.getString("parameters.multipleDecodingFail",
                    Integer.toString(decodeFailCount)));
        }
    }