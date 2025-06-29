public Object instantiate(Class type, Configuration conf, boolean fatal) {
        Object obj = newInstance(_name, type, conf, fatal);
        
        // ensure plugin value is compatible with plugin type
        if (obj != null && !type.isAssignableFrom(obj.getClass())) {
            Log log = (conf == null) ? null : conf.getConfigurationLog();
            String msg = getIncompatiblePluginMessage(obj, type);
            if (log != null && log.isErrorEnabled()) {
            	log.error(msg);
            }
            if (fatal) {
            	throw new ParseException(msg);
            }
            return null;
        }
        
        Configurations.configureInstance(obj, conf, _props,
            (fatal) ? getProperty() : null);
        if (_singleton)
            set(obj, true);
        return obj;
    }