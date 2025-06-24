protected String sanitiseWindowId(String windowId)
    {
        return windowId.replace('(', '_');
    }