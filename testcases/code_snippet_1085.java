protected Object readResolve()
        throws ObjectStreamException {
        AbstractBrokerFactory factory = getPooledFactoryForKey(_poolKey);
        if (factory != null)
            return factory;

        // reset these transient fields to empty values
        _transactional = new ConcurrentHashMap<Object,Collection<Broker>>();
        _brokers = newBrokerSet();

        makeReadOnly();
        return this;
    }