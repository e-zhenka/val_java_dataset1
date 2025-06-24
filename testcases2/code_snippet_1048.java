private void initialize() throws TransformerFactoryConfigurationError, TransformerConfigurationException,
            ParserConfigurationException {
        if (!xslIsInitialized) {
            TransformerFactory transformerFactory = createTransformer();

            nunitTransformer = transformerFactory.newTransformer(new StreamSource(this.getClass().getResourceAsStream(NUNIT_TO_JUNIT_XSLFILE_STR)));
            writerTransformer = transformerFactory.newTransformer();

            DocumentBuilderFactory factory = createDocumentBuilderFactory();
            xmlDocumentBuilder = factory.newDocumentBuilder();

            xslIsInitialized = true;
        }
    }