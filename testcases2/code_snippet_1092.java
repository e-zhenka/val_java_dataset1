private Document createDocFromMessage(InputStream message)
            throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbfactory.newDocumentBuilder();
        return builder.parse(new InputSource(message));
    }