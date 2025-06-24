protected Object readResolve()
        throws ObjectStreamException {
        AbstractBrokerFactory factory = getPooledFactory(_conf);
        if (factory != null)
            return factory;

        // reset these transient fields to empty values
        _transactional = new ConcurrentHashMap();
        _brokers = new ConcurrentReferenceHashSet(
                ConcurrentReferenceHashSet.WEAK);

        makeReadOnly();
        return this;
    }