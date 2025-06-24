public void updateByXml(final InputStream source) throws IOException, ServletException {
        checkPermission(CONFIGURE);
        Node previous = getNode();
        if (previous == null) {
            throw HttpResponses.notFound();
        }
        Node result = (Node)Jenkins.XSTREAM2.fromXML(source);
        if (previous.getClass() != result.getClass()) {
            // ensure node type doesn't change
            throw HttpResponses.errorWithoutStack(SC_BAD_REQUEST, "Node types do not match");
        }
        Jenkins.get().getNodesObject().replaceNode(previous, result);
    }