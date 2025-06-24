@Override
	public ParseResults call() {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser saxParser = spf.newSAXParser();
			XMLReader reader = saxParser.getXMLReader();

			// Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
			// Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities

			// Using the SAXParserFactory's setFeature
			spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			// Using the XMLReader's setFeature
			reader.setFeature("http://xml.org/sax/features/external-general-entities", false);

			// Xerces 2 only - http://xerces.apache.org/xerces-j/features.html#external-general-entities
			spf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

			saxParser.parse(xmlInputStream, handler);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new ReportPortalException(ErrorType.PARSING_XML_ERROR, e.getMessage());
		}
		return new ParseResults(handler.getStartSuiteTime(), handler.getCommonDuration());
	}