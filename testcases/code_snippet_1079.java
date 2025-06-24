public void updateByXml(final InputStream source) throws IOException, ServletException {
        checkPermission(CONFIGURE);
        Node result = (Node)Jenkins.XSTREAM2.fromXML(source);
        Jenkins.get().getNodesObject().replaceNode(this.getNode(), result);
    }