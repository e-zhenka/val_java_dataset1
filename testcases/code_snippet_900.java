public Object instantiate(Class type, Configuration conf, boolean fatal) {
        Object obj = newInstance(_name, type, conf, fatal);
        Configurations.configureInstance(obj, conf, _props,
            (fatal) ? getProperty() : null);
        if (_singleton)
            set(obj, true);
        return obj;
    }