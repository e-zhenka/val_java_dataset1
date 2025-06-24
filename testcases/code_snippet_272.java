public void loadXmiFile(File xmiCasFile) {
    try {
      setXcasFileOpenDir(xmiCasFile.getParentFile());
      Timer time = new Timer();
      time.start();
      SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
      saxParserFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      SAXParser parser = saxParserFactory.newSAXParser();
      XmiCasDeserializer xmiCasDeserializer = new XmiCasDeserializer(getCas().getTypeSystem());
      getCas().reset();
      parser.parse(xmiCasFile, xmiCasDeserializer.getXmiCasHandler(getCas(), true));
      time.stop();
      handleSofas();

      setTitle("XMI CAS");
      updateIndexTree(true);
      setRunOnCasEnabled();
      setEnableCasFileReadingAndWriting();
      setStatusbarMessage("Done loading XMI CAS file in " + time.getTimeSpan() + ".");
    } catch (Exception e) {
      e.printStackTrace();
      handleException(e);
    }
  }