public static Connection connectSync(final ProtocolConnectionConfiguration configuration) throws IOException {
        long timeoutMillis = configuration.getConnectionTimeout();
        CallbackHandler handler = configuration.getCallbackHandler();
        final CallbackHandler actualHandler;
        ProtocolTimeoutHandler timeoutHandler = configuration.getTimeoutHandler();
        // Note: If a client supplies a ProtocolTimeoutHandler it is taking on full responsibility for timeout management.
        if (timeoutHandler == null) {
            GeneralTimeoutHandler defaultTimeoutHandler = new GeneralTimeoutHandler();
            // No point wrapping our AnonymousCallbackHandler.
            actualHandler = handler != null ? new WrapperCallbackHandler(defaultTimeoutHandler, handler) : null;
            timeoutHandler = defaultTimeoutHandler;
        } else {
            actualHandler = handler;
        }

        final IoFuture<Connection> future = connect(actualHandler, configuration);

        IoFuture.Status status = timeoutHandler.await(future, timeoutMillis);

        if (status == IoFuture.Status.DONE) {
            return future.get();
        }
        if (status == IoFuture.Status.FAILED) {
            throw ProtocolLogger.ROOT_LOGGER.failedToConnect(configuration.getUri(), future.getException());
        }
        throw ProtocolLogger.ROOT_LOGGER.couldNotConnect(configuration.getUri());
    }