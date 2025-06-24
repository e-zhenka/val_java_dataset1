@PresetData(DataSet.ANONYMOUS_READONLY)
    @Email("http://www.nabble.com/Launching-slave-by-JNLP-with-Active-Directory-plugin-and-matrix-security-problem-td18980323.html")
    public void test() throws Exception {
        jenkins.setNodes(Collections.singletonList(createNewJnlpSlave("test")));
        HudsonTestCase.WebClient wc = new WebClient();
        HtmlPage p = wc.login("alice").goTo("computer/test/");

        // this fresh WebClient doesn't have a login cookie and represent JNLP launcher
        HudsonTestCase.WebClient jnlpAgent = new WebClient();

        // parse the JNLP page into DOM to list up the jars.
        XmlPage jnlp = (XmlPage) wc.goTo("computer/test/slave-agent.jnlp","application/x-java-jnlp-file");
        URL baseUrl = jnlp.getWebResponse().getUrl();
        Document dom = new DOMReader().read(jnlp.getXmlDocument());
        for( Element jar : (List<Element>)dom.selectNodes("//jar") ) {
            URL url = new URL(baseUrl,jar.attributeValue("href"));
            System.out.println(url);
            
            // now make sure that these URLs are unprotected
            Page jarResource = jnlpAgent.getPage(url);
            assertTrue(jarResource.getWebResponse().getContentType().toLowerCase(Locale.ENGLISH).startsWith("application/"));
        }


        try {
            jnlp = (XmlPage) jnlpAgent.goTo("computer/test/slave-agent.jnlp", "application/x-java-jnlp-file");
            fail("anonymous users must not be able to get secrets");
        } catch (FailingHttpStatusCodeException x) {
            assertEquals(HttpURLConnection.HTTP_FORBIDDEN, x.getStatusCode());
        }
    }