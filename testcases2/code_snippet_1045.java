protected Result findResult(String path, String resultCode, String ext, ActionContext actionContext,
                                Map<String, ResultTypeConfig> resultsByExtension) {
        try {
            boolean traceEnabled = LOG.isTraceEnabled();
            if (traceEnabled)
                LOG.trace("Checking ServletContext for [#0]", path);

            if (servletContext.getResource(path) != null) {
                if (traceEnabled)
                    LOG.trace("Found");
                return buildResult(path, resultCode, resultsByExtension.get(ext), actionContext);
            }

            if (traceEnabled)
                LOG.trace("Checking ClasLoader for #0", path);

            String classLoaderPath = path.startsWith("/") ? path.substring(1, path.length()) : path;
            if (ClassLoaderUtil.getResource(classLoaderPath, getClass()) != null) {
                if (traceEnabled)
                    LOG.trace("Found");
                return buildResult(path, resultCode, resultsByExtension.get(ext), actionContext);
            }
        } catch (MalformedURLException e) {
            if (LOG.isErrorEnabled())
                LOG.error("Unable to parse template path: [#0] skipping...", path);
        }

        return null;
    }