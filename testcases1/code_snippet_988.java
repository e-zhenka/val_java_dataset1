default Argument<?> getErrorType(MediaType mediaType) {
        if (mediaType.equals(MediaType.APPLICATION_JSON_TYPE)) {
            return Argument.of(JsonError.class);
        } else if (mediaType.equals(MediaType.APPLICATION_VND_ERROR_TYPE)) {
            return Argument.of(VndError.class);
        } else {
            return Argument.of(String.class);
        }
    }