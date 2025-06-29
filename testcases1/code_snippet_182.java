public static @Nonnull ConfidentialStore get() {
        if (TEST!=null) return TEST.get();

        Lookup lookup = Jenkins.getInstance().lookup;
        ConfidentialStore cs = lookup.get(ConfidentialStore.class);
        if (cs==null) {
            try {
                List<ConfidentialStore> r = (List) Service.loadInstances(ConfidentialStore.class.getClassLoader(), ConfidentialStore.class);
                if (!r.isEmpty())
                    cs = r.get(0);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to list up ConfidentialStore implementations",e);
                // fall through
            }

            if (cs==null)
                try {
                    cs = new DefaultConfidentialStore();
                } catch (Exception e) {
                    // if it's still null, bail out
                    throw new Error(e);
                }

            cs = lookup.setIfNull(ConfidentialStore.class,cs);
        }
        return cs;
    }