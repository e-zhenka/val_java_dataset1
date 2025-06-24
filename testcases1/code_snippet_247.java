protected void parseInputStream(InputStream stream) {
		SAXParserFactory pf = SAXParserFactory.newInstance();
		try {
			// Prevent XXE
			pf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			pf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			pf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

			SAXParser parser = pf.newSAXParser();
			parser.parse(stream, new SAXHandler());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Build the source and sink lists
		buildSourceSinkLists();
	}