private void addJsr160DispatcherIfExternallyConfigured(Configuration pConfig) {
        for (String param : new String[] {
            System.getProperty("org.jolokia.jsr160ProxyEnabled"),
            System.getenv("JOLOKIA_JSR160_PROXY_ENABLED")
        }) {
            if (param !=null && (param.isEmpty() || Boolean.parseBoolean(param))){
                String dispatchers = pConfig.get(ConfigKey.DISPATCHER_CLASSES);

                pConfig.updateGlobalConfiguration(
                    Collections.singletonMap(
                        ConfigKey.DISPATCHER_CLASSES.getKeyValue(),
                        (dispatchers != null ? dispatchers + "," : "") + "org.jolokia.jsr160.Jsr160RequestDispatcher"));
                return;
            }
        }
    }