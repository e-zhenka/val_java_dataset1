protected String sanitiseWindowId(String windowId)
    {
        return windowId.replace('(', '_').replace('<', '_').replace('&', '_');
    }