public Object eval(String xml, String path, QName qname) {
    if (xml == null || path == null || qname == null) {
      return null;
    }

    if (xml.length() == 0 || path.length() == 0) {
      return null;
    }

    if (!path.equals(oldPath)) {
      try {
        expression = xpath.compile(path);
      } catch (XPathExpressionException e) {
        expression = null;
      }
      oldPath = path;
    }

    if (expression == null) {
      return null;
    }

    if (builder == null){
      try {
        initializeDocumentBuilderFactory();
        builder = dbf.newDocumentBuilder();
      } catch (ParserConfigurationException e) {
        throw new RuntimeException("Error instantiating DocumentBuilder, cannot build xml parser", e);
      }
    }

    reader.set(xml);

    try {
      return expression.evaluate(builder.parse(inputSource), qname);
    } catch (XPathExpressionException e) {
      throw new RuntimeException ("Invalid expression '" + oldPath + "'", e);
    } catch (Exception e) {
      throw new RuntimeException("Error loading expression '" + oldPath + "'", e);
    }
  }