protected String cleanupActionName(final String rawActionName) {
        if (rawActionName.matches(allowedActionNames)) {
            return rawActionName;
        } else {
            String cleanActionName = rawActionName;
            for(String chunk : rawActionName.split(allowedActionNames)) {
                cleanActionName = cleanActionName.replace(chunk, "");
            }
            return cleanActionName;
        }
    }