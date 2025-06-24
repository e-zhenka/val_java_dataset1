@Override
    public HttpContent getContent(String pathInContext,int maxBufferSize)
        throws IOException
    {
        // try loading the content from our factory.
        Resource resource=_factory.getResource(pathInContext);
        HttpContent loaded = load(pathInContext,resource,maxBufferSize);
        return loaded;
    }