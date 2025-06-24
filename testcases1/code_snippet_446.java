public static Document xmlText2GenericDom(InputStream is, Document emptyDoc)
            throws SAXException, ParserConfigurationException, IOException
    {
        SAXParser parser = SAXHelper.saxFactory.newSAXParser();

        Sax2Dom handler = new Sax2Dom(emptyDoc);

        parser.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
        parser.parse(is, handler);

        return (Document) handler.getDOM();
    }