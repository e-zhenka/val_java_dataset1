void doMaxAttributesFails() throws Exception
   {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      System.out.println("dbf.getClass(): " + dbf.getClass());
      if ("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl".equals(dbf.getClass().getName()))
      {
         System.out.println("Testing with Red Hat version of Xerces, skipping max attributes test");
         return;
      }
      System.out.println("entering doMaxAttributesFails()");
      ClientRequest request = new ClientRequest(generateURL("/test"));
      request.body("application/xml", bigAttributeDoc);
      ClientResponse<?> response = request.post();
      System.out.println("doMaxAttributesFails() status: " + response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("doMaxAttributesFails() result: " + entity);
      Assert.assertEquals(400, response.getStatus());
      Assert.assertTrue(entity.contains("org.xml.sax.SAXParseException"));
      Assert.assertTrue(entity.contains("has more than \"10,00"));
      int pos = entity.indexOf("has more than \"10,00");
      Assert.assertTrue(entity.substring(pos).contains("attributes"));
   }