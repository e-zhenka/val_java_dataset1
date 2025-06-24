private Document getXmlDoc() {
    if (this.xmlDoc != null) {
      return this.xmlDoc;
    }

    try {
      this.xmlDoc = DocumentBuilderFactory
        .newInstance()
        .newDocumentBuilder()
        .parse(new ByteArrayInputStream(this.xmlString.getBytes(StandardCharsets.UTF_8)));
      return xmlDoc;
    } catch (SAXException | IOException | ParserConfigurationException e) {
      throw new RuntimeException("非法的xml文本内容：" + this.xmlString);
    }

  }