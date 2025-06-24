private String guard(String filename) {
            String guarded = filename.replace(":", "_");
            if (LOG.isDebugEnabled()) {
                LOG.debug("guarded " + filename + " to " + guarded);
            }
            return guarded;
        }