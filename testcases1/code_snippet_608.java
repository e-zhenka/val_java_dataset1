protected String parseFor(final String infoName) throws ParserConfigurationException, SAXException, IOException {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(false);

            SAXParser saxParser = factory.newSAXParser();
            ReadInfoHandler riHandler = new ReadInfoHandler(infoName);
            try {
                saxParser.parse(this.reportFile, riHandler);
            } catch (BreakParsingException e) {
                // break parsing
            }
            return riHandler.getInfo();
        }