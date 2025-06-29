@Activate
    @SuppressWarnings("unused")
    protected void activate() {
        factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        try {
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        } catch (Exception e) {
            LOGGER.error("SAX parser configuration error: " + e.getMessage(), e);
        }
    }