public static XMLReader createXmlReader() throws SAXException, ParserConfigurationException {
		SAXParserFactory spf = SAXParserFactory.newInstance();

		// Create a JAXP SAXParser
		SAXParser saxParser = spf.newSAXParser();

		// Get the encapsulated SAX XMLReader
		XMLReader reader = saxParser.getXMLReader();

		// set default features
		reader.setFeature("http://xml.org/sax/features/namespaces", true);

		return reader;
	}