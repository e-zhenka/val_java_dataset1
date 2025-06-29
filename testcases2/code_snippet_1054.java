public DIHConfiguration loadDataConfig(InputSource configFile) {

    DIHConfiguration dihcfg = null;
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setValidating(false);
      
      // only enable xinclude, if XML is coming from safe source (local file)
      // and a a SolrCore and SystemId is present (makes no sense otherwise):
      if (core != null && configFile.getSystemId() != null) {
        try {
          dbf.setXIncludeAware(true);
          dbf.setNamespaceAware(true);
        } catch( UnsupportedOperationException e ) {
          LOG.warn( "XML parser doesn't support XInclude option" );
        }
      }
      
      DocumentBuilder builder = dbf.newDocumentBuilder();
      // only enable xinclude / external entities, if XML is coming from
      // safe source (local file) and a a SolrCore and SystemId is present:
      if (core != null && configFile.getSystemId() != null) {
        builder.setEntityResolver(new SystemIdResolver(core.getResourceLoader()));
      } else {
        // Don't allow external entities without having a system ID:
        builder.setEntityResolver(EmptyEntityResolver.SAX_INSTANCE);
      }
      builder.setErrorHandler(XMLLOG);
      Document document;
      try {
        document = builder.parse(configFile);
      } finally {
        // some XML parsers are broken and don't close the byte stream (but they should according to spec)
        IOUtils.closeQuietly(configFile.getByteStream());
      }

      dihcfg = readFromXml(document);
      LOG.info("Data Configuration loaded successfully");
    } catch (Exception e) {
      throw new DataImportHandlerException(SEVERE,
              "Data Config problem: " + e.getMessage(), e);
    }
    for (Entity e : dihcfg.getEntities()) {
      if (e.getAllAttributes().containsKey(SqlEntityProcessor.DELTA_QUERY)) {
        isDeltaImportSupported = true;
        break;
      }
    }
    return dihcfg;
  }