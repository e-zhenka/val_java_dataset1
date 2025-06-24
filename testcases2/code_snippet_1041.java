private void addInputFilter(InputFilter[] inputFilters, String encodingName) {

        // Parsing trims and converts to lower case.

        if (encodingName.equals("chunked")) {
            inputBuffer.addActiveFilter(inputFilters[Constants.CHUNKED_FILTER]);
            contentDelimitation = true;
        } else {
            for (int i = pluggableFilterIndex; i < inputFilters.length; i++) {
                if (inputFilters[i].getEncodingName().toString().equals(encodingName)) {
                    inputBuffer.addActiveFilter(inputFilters[i]);
                    return;
                }
            }
            // Unsupported transfer encoding
            // 501 - Unimplemented
            response.setStatus(501);
            setErrorState(ErrorState.CLOSE_CLEAN, null);
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("http11processor.request.prepare") +
                          " Unsupported transfer encoding [" + encodingName + "]");
            }
        }
    }