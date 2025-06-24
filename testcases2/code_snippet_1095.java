private Document parseXml(String xmlContent) {
		try {
			DocumentBuilder builder = XmlSecurity.getSecureDbf().newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(xmlContent)));

			document.getDocumentElement().normalize();

			return document;
		} catch (Exception e) {
			throw new JadxRuntimeException("Can not parse xml content", e);
		}
	}