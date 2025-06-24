public static OMElement buildOMElement(String payload) throws RegistryException {

        OMElement element = getOMElementFromString(payload);
        element.build();
        return element;
    }