@Activate
    @SuppressWarnings("unused")
    protected void activate() {
        factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
    }