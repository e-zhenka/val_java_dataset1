public void parseDmozFile(File dmozFile, int subsetDenom,
      boolean includeAdult, int skew, Pattern topicPattern) 
              throws IOException, SAXException, ParserConfigurationException {

    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
    parserFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
    parserFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    SAXParser parser = parserFactory.newSAXParser();
    XMLReader reader = parser.getXMLReader();
    reader.setFeature("http://xml.org/sax/features/external-general-entities", false);

    // Create our own processor to receive SAX events
    RDFProcessor rp = new RDFProcessor(reader, subsetDenom, includeAdult, skew,
        topicPattern);
    reader.setContentHandler(rp);
    reader.setErrorHandler(rp);
    LOG.info("skew = " + rp.hashSkew);

    //
    // Open filtered text stream. The TextFilter makes sure that
    // only appropriate XML-approved Text characters are received.
    // Any non-conforming characters are silently skipped.
    //
    try (XMLCharFilter in = new XMLCharFilter(new BufferedReader(
        new InputStreamReader(new BufferedInputStream(new FileInputStream(
            dmozFile)), "UTF-8")))) {
      InputSource is = new InputSource(in);
      reader.parse(is);
    } catch (Exception e) {
      if (LOG.isErrorEnabled()) {
        LOG.error(e.toString());
      }
      System.exit(0);
    }
  }