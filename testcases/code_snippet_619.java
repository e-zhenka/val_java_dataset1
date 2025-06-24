static Document parseXML(InputStream pXmlFile) throws ParserException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = null;
    try {
      db = dbf.newDocumentBuilder();
    }
    catch (Exception se) {
      throw new ParserException("XML Parser configuration error", se);
    }
    org.w3c.dom.Document doc = null;
    try {
      doc = db.parse(pXmlFile);
    }
    catch (Exception se) {
      throw new ParserException("Error parsing XML stream:" + se, se);
    }
    return doc;
  }