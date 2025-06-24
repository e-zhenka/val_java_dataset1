public static ClusterNodeInformation unmarshal(final InputStream is) throws JAXBException {
        final Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();
        return (ClusterNodeInformation) unmarshaller.unmarshal(is);
    }