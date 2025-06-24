@Override
    protected Settings nodeSettings(int nodeOrdinal) {
        return ImmutableSettings.builder()
                .putArray(URLRepository.ALLOWED_URLS_SETTING, "http://snapshot.test*")
                .put(InternalNode.HTTP_ENABLED, true)
                .put(super.nodeSettings(nodeOrdinal)).build();
    }