@Override
    public void setServerUrl(final String url) {
        try {
            final URISupport.CompositeData compositeData = URISupport.parseComposite(URLs.uri(url));
            if ("vm".equals(compositeData.getScheme())) {
                super.setServerUrl(URISupport.addParameters(URLs.uri(url), PREVENT_CREATION_PARAMS).toString());
                return;
            }
        } catch (URISyntaxException e) {
            // if we hit an exception, we'll log this and simple pass the URL we were given to ActiveMQ.
            LOGGER.error("Error occurred while processing ActiveMQ ServerUrl: " + url, e);
        }

        super.setServerUrl(url);
    }