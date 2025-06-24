public static C3P0Config extractXmlConfigFromInputStream(InputStream is) throws Exception
    {
        DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = fact.newDocumentBuilder();
        Document doc = db.parse( is );

        return extractConfigFromXmlDoc(doc);
    }