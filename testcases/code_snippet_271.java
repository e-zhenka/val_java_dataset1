private static Document loadConfigFile(SolrResourceLoader resourceLoader, String parseContextConfigLoc) throws Exception {
    try (InputStream in = resourceLoader.openResource(parseContextConfigLoc)) {
      return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in, parseContextConfigLoc);
    }
  }