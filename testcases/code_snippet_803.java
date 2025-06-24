@Override
    protected void onExchange(Exchange exchange) throws Exception {
        String newResourceUri = exchange.getIn().getHeader(XsltConstants.XSLT_RESOURCE_URI, String.class);
        if (newResourceUri != null) {
            exchange.getIn().removeHeader(XsltConstants.XSLT_RESOURCE_URI);

            LOG.trace("{} set to {} creating new endpoint to handle exchange", XsltConstants.XSLT_RESOURCE_URI, newResourceUri);
            XsltEndpoint newEndpoint = findOrCreateEndpoint(getEndpointUri(), newResourceUri);
            newEndpoint.onExchange(exchange);
        } else {
            if (!cacheStylesheet || cacheCleared) {
                loadResource(resourceUri);
            }
            super.onExchange(exchange);
        }
    }