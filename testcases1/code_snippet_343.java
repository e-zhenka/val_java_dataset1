private boolean verifySecret(String action, int bridgeSecret) throws IllegalAccessException {
        if (!jsMessageQueue.isBridgeEnabled()) {
            if (bridgeSecret == -1) {
                Log.d(LOG_TAG, action + " call made before bridge was enabled.");
            } else {
                Log.d(LOG_TAG, "Ignoring " + action + " from previous page load.");
            }
            return false;
        }
        // Bridge secret wrong and bridge not due to it being from the previous page.
        if (expectedBridgeSecret < 0 || bridgeSecret != expectedBridgeSecret) {
            Log.e(LOG_TAG, "Bridge access attempt with wrong secret token, possibly from malicious code. Disabling exec() bridge!");
            clearBridgeSecret();
            throw new IllegalAccessException();
        }
        return true;
    }