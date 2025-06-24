public static Document parseDocument(InputSource source) throws XMLException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder xmlBuilder = dbf.newDocumentBuilder();
            return xmlBuilder.parse(source);
        } catch (Exception er) {
            throw new XMLException("Error parsing XML document", er);
        }
    }