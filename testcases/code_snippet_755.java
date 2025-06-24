public static Document xmlText2GenericDom(InputStream is, Document emptyDoc)
            throws SAXException, ParserConfigurationException, IOException
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);

        SAXParser parser = factory.newSAXParser();

        Sax2Dom handler = new Sax2Dom(emptyDoc);

        parser.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
        parser.parse(is, handler);

        return (Document) handler.getDOM();
    }