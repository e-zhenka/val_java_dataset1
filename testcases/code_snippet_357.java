public T readFrom(Class<T> clazz, Type t, Annotation[] a, MediaType mt, 
                         MultivaluedMap<String, String> headers, InputStream is) 
        throws IOException {
        Parser parser = ATOM_ENGINE.getParser();
        synchronized (parser) {
            ParserOptions options = parser.getDefaultParserOptions();
            if (options != null) {
                options.setAutodetectCharset(autodetectCharset);
                options.setResolveEntities(false);
            }
        }
        Document<T> doc = parser.parse(is);
        return doc.getRoot();
    }