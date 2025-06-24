public Document readFrom(Class<Document> clazz, Type type,
                            Annotation[] annotations, MediaType mediaType,
                            MultivaluedMap<String, String> headers, InputStream input)
           throws IOException, WebApplicationException
   {
      try
      {
         documentBuilder.setExpandEntityReferences(expandEntityReferences);
         documentBuilder.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, enableSecureProcessingFeature);
         documentBuilder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", disableDTDs);
         return documentBuilder.newDocumentBuilder().parse(input);
      }
      catch (Exception e)
      {
         throw new ReaderException(e);
      }
   }