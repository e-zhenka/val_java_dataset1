public static Document parseDocument(InputSource source) throws XMLException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder xmlBuilder = dbf.newDocumentBuilder();
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            return xmlBuilder.parse(source);
        } catch (Exception er) {
            throw new XMLException("Error parsing XML document", er);
        }
    }