protected String cleanupActionName(final String rawActionName) {
        if (rawActionName.matches(allowedActionNames)) {
            return rawActionName;
        } else {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Action [#0] do not match allowed action names pattern [#1], cleaning it up!",
                        rawActionName, allowedActionNames);
            }
            String cleanActionName = rawActionName;
            for(String chunk : rawActionName.split(allowedActionNames)) {
                cleanActionName = cleanActionName.replace(chunk, "");
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Cleaned action name [#0]", cleanActionName);
            }
            return cleanActionName;
        }
    }