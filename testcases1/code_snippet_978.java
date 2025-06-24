private void close() {
        connectionState.set(ConnectionState.CLOSED);
        try {
            socketWrapper.close();
        } catch (IOException ioe) {
            log.debug(sm.getString("upgradeHandler.socketCloseFailed"), ioe);
        }
    }