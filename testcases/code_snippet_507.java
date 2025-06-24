public static void setFeaturesBySystemProperty(SAXParserFactory factory)
                throws SAXException, ParserConfigurationException {

            final boolean enableExternalDtdLoad = Boolean.parseBoolean(
                System.getProperty(ENABLE_EXTERNAL_DTD_LOAD, "false"));

            factory.setFeature(LOAD_EXTERNAL_DTD, enableExternalDtdLoad);
            factory.setFeature(EXTERNAL_GENERAL_ENTITIES, enableExternalDtdLoad);
            factory.setFeature(EXTERNAL_PARAMETER_ENTITIES, enableExternalDtdLoad);
        }