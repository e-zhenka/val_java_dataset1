private void doTestRewrite(String config, String request, String expectedURI,
            String expectedQueryString, String expectedAttributeValue) throws Exception {

        Tomcat tomcat = getTomcatInstance();

        // No file system docBase required
        Context ctx = tomcat.addContext("", null);

        RewriteValve rewriteValve = new RewriteValve();
        ctx.getPipeline().addValve(rewriteValve);

        rewriteValve.setConfiguration(config);

        Tomcat.addServlet(ctx, "snoop", new SnoopServlet());
        ctx.addServletMappingDecoded("/a/%5A", "snoop");
        ctx.addServletMappingDecoded("/c/*", "snoop");
        Tomcat.addServlet(ctx, "default", new DefaultServlet());
        ctx.addServletMappingDecoded("/", "default");

        tomcat.start();

        ByteChunk res = getUrl("http://localhost:" + getPort() + request);
        res.setCharset(StandardCharsets.UTF_8);

        String body = res.toString();
        RequestDescriptor requestDesc = SnoopResult.parse(body);
        String requestURI = requestDesc.getRequestInfo("REQUEST-URI");
        Assert.assertEquals(expectedURI, requestURI);

        if (expectedQueryString != null) {
            String queryString = requestDesc.getRequestInfo("REQUEST-QUERY-STRING");
            Assert.assertEquals(expectedQueryString, queryString);
        }

        if (expectedAttributeValue != null) {
            String attributeValue = requestDesc.getAttribute("X-Test");
            Assert.assertEquals(expectedAttributeValue, attributeValue);
        }
    }