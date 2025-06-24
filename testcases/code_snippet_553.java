protected DocumentBuilder getDocumentBuilder() throws IOException {
        DocumentBuilder result = null;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(isNamespaceAware());
            dbf.setValidating(isValidatingDtd());
            dbf.setCoalescing(isCoalescing());
            dbf.setExpandEntityReferences(isExpandingEntityRefs());
            dbf.setIgnoringComments(isIgnoringComments());
            dbf.setIgnoringElementContentWhitespace(isIgnoringExtraWhitespaces());

            try {
                dbf.setXIncludeAware(isXIncludeAware());
            } catch (UnsupportedOperationException uoe) {
                Context.getCurrentLogger().log(Level.FINE,
                        "The JAXP parser doesn't support XInclude.", uoe);
            }

            // [ifndef android]
            javax.xml.validation.Schema xsd = getSchema();

            if (xsd != null) {
                dbf.setSchema(xsd);
            }
            // [enddef]

            result = dbf.newDocumentBuilder();
            result.setEntityResolver(getEntityResolver());
            result.setErrorHandler(getErrorHandler());
        } catch (ParserConfigurationException pce) {
            throw new IOException("Couldn't create the empty document: "
                    + pce.getMessage());
        }

        return result;
    }