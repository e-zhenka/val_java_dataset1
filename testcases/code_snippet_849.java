public DIHConfiguration loadDataConfig(InputSource configFile) {

    DIHConfiguration dihcfg = null;
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      
      // only enable xinclude, if a a SolrCore and SystemId is present (makes no sense otherwise)
      if (core != null && configFile.getSystemId() != null) {
        try {
          dbf.setXIncludeAware(true);
          dbf.setNamespaceAware(true);
        } catch( UnsupportedOperationException e ) {
          LOG.warn( "XML parser doesn't support XInclude option" );
        }
      }
      
      DocumentBuilder builder = dbf.newDocumentBuilder();
      if (core != null)
        builder.setEntityResolver(new SystemIdResolver(core.getResourceLoader()));
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