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

        Connection result = checkFuture(status, future, configuration);
        if (result == null) {
            // Did not complete in time; tell remoting we don't want it
            future.cancel();
            // In case the future completed between when we waited for it and when we cancelled,
            // close any connection that was established. We don't want to risk using a
            // Connection after we told remoting to cancel, and if we don't use it we must close it.
            Connection toClose = checkFuture(future.getStatus(), future, configuration);
            StreamUtils.safeClose(toClose);

            throw ProtocolLogger.ROOT_LOGGER.couldNotConnect(configuration.getUri());
        }
        return result;
    }