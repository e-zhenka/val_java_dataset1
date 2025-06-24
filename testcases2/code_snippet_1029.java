private void initialize() throws TransformerFactoryConfigurationError, TransformerConfigurationException,
            ParserConfigurationException {
        if (!xslIsInitialized) {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            nunitTransformer = transformerFactory.newTransformer(new StreamSource(this.getClass().getResourceAsStream(NUNIT_TO_JUNIT_XSLFILE_STR)));
            writerTransformer = transformerFactory.newTransformer();
    
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            xmlDocumentBuilder = factory.newDocumentBuilder();
            
            xslIsInitialized = true;
        }
    }