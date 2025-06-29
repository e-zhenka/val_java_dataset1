private void init(ErrorDispatcher err) throws JasperException {
        if (initialized)
            return;

        String blockExternalString = ctxt.getInitParameter(
                Constants.XML_BLOCK_EXTERNAL_INIT_PARAM);
        boolean blockExternal;
        if (blockExternalString == null) {
            blockExternal = true;
        } else {
            blockExternal = Boolean.parseBoolean(blockExternalString);
        }

        TagPluginParser parser = new TagPluginParser(ctxt, blockExternal);

        try {
            Enumeration<URL> urls =
                    ctxt.getClassLoader().getResources(META_INF_JASPER_TAG_PLUGINS_XML);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    parser.parse(url);
                }
            }

            URL url = ctxt.getResource(TAG_PLUGINS_XML);
            if (url != null) {
                parser.parse(url);
            }
        } catch (IOException | SAXException e) {
            throw new JasperException(e);
        }

        Map<String, String> plugins = parser.getPlugins();
        tagPlugins = new HashMap<>(plugins.size());
        for (Map.Entry<String, String> entry : plugins.entrySet()) {
            try {
                String tagClass = entry.getKey();
                String pluginName = entry.getValue();
                Class<?> pluginClass = ctxt.getClassLoader().loadClass(pluginName);
                TagPlugin plugin = (TagPlugin) pluginClass.newInstance();
                tagPlugins.put(tagClass, plugin);
            } catch (Exception e) {
                err.jspError(e);
            }
        }
        initialized = true;
    }