public static C3P0Config extractXmlConfigFromInputStream(InputStream is) throws Exception
    {
        DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
	fact.setExpandEntityReferences(false);
        DocumentBuilder db = fact.newDocumentBuilder();
        Document doc = db.parse( is );

        return extractConfigFromXmlDoc(doc);
    }