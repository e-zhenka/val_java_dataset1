protected Configuration createConfiguration(ServletContext servletContext) throws TemplateException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_0);

        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);

        if (mruMaxStrongSize > 0) {
            configuration.setSetting(Configuration.CACHE_STORAGE_KEY, "strong:" + mruMaxStrongSize);
        }
        if (templateUpdateDelay != null) {
            configuration.setSetting(Configuration.TEMPLATE_UPDATE_DELAY_KEY, templateUpdateDelay);
        }
        if (encoding != null) {
            configuration.setDefaultEncoding(encoding);
        }
        configuration.setLocalizedLookup(false);
        configuration.setWhitespaceStripping(true);

        return configuration;
    }