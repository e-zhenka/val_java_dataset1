@Override
        public void handleEvent(StreamConnection channel) {
            final Map<String, String> headers = new HashMap<String, String>();
            headers.put(UPGRADE, "jboss-remoting");
            final String secKey = createSecKey();
            headers.put(SEC_JBOSS_REMOTING_KEY, secKey);

            IoFuture<T> upgradeFuture = HttpUpgrade.performUpgrade(type.cast(channel), uri, headers, upgradeChannel -> {
                ChannelListeners.invokeChannelListener(upgradeChannel, openListener);
            }, new RemotingHandshakeChecker(secKey));
            upgradeFuture.addNotifier( new IoFuture.HandlingNotifier<T, FutureResult<T>>() {

                @Override
                public void handleCancelled(FutureResult<T> attachment) {
                    attachment.setCancelled();
                }

                @Override
                public void handleFailed(IOException exception, FutureResult<T> attachment) {
                    attachment.setException(exception);
                }

                @Override
                public void handleDone(T data, FutureResult<T> attachment) {
                    attachment.setResult(data);
                }

            }, futureResult);
        }