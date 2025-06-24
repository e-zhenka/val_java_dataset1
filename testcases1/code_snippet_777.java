private void addJsr160DispatcherIfExternallyConfigured(Configuration pConfig) {
        String dispatchers = pConfig.get(ConfigKey.DISPATCHER_CLASSES);
        String jsr160DispatcherClass = "org.jolokia.jsr160.Jsr160RequestDispatcher";

        if (dispatchers == null || !dispatchers.contains(jsr160DispatcherClass)) {
            for (String param : new String[]{
                System.getProperty("org.jolokia.jsr160ProxyEnabled"),
                System.getenv("JOLOKIA_JSR160_PROXY_ENABLED")
            }) {
                if (param != null && (param.isEmpty() || Boolean.parseBoolean(param))) {
                    {
                        pConfig.updateGlobalConfiguration(
                            Collections.singletonMap(
                                ConfigKey.DISPATCHER_CLASSES.getKeyValue(),
                                (dispatchers != null ? dispatchers + "," : "") + jsr160DispatcherClass));
                    }
                    return;
                }
            }
            if (dispatchers == null) {
                // We add a breaking dispatcher to avoid silently ignoring a JSR160 proxy request
                // when it is now enabled
                pConfig.updateGlobalConfiguration(Collections.singletonMap(
                    ConfigKey.DISPATCHER_CLASSES.getKeyValue(),
                    Jsr160ProxyNotEnabledByDefaultAnymoreDispatcher.class.getCanonicalName()));
            }
        }
    }