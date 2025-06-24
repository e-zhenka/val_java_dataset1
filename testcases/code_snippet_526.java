public static @Nonnull ConfidentialStore get() {
        if (TEST!=null) return TEST.get();
        return Jenkins.getInstance().getExtensionList(ConfidentialStore.class).get(0);
    }