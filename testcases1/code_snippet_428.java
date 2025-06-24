protected SchemaFactory createSchemaFactory() {
        SchemaFactory factory = SchemaFactory.newInstance(schemaLanguage);
        if (getResourceResolver() != null) {
            factory.setResourceResolver(getResourceResolver());
        }
        return factory;
    }