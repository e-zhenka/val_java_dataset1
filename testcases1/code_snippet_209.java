public static ClusterNodeInformation unmarshal(final InputStream is) throws JAXBException {
        try {
            final Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();
            final XMLStreamReader xsr = XmlUtils.createSafeReader(is);
            return (ClusterNodeInformation) unmarshaller.unmarshal(xsr);
        } catch (XMLStreamException e) {
            throw new JAXBException("Error unmarshalling the cluster node information", e);
        }
    }