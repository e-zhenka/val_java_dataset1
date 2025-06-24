protected Channel jnlpConnect(SlaveComputer computer) throws InterruptedException, IOException {
            final String nodeName = computer.getName();
            final OutputStream log = computer.openLogFile();
            PrintWriter logw = new PrintWriter(log,true);
            logw.println("JNLP agent connected from "+ socket.getInetAddress());

            try {
                ChannelBuilder cb = createChannelBuilder(nodeName);

                for (ChannelConfigurator cc : ChannelConfigurator.all()) {
                    cc.onChannelBuilding(cb, computer);
                }

                computer.setChannel(cb.withHeaderStream(log).build(socket), log,
                    new Listener() {
                        @Override
                        public void onClosed(Channel channel, IOException cause) {
                            if(cause!=null)
                                LOGGER.log(Level.WARNING, Thread.currentThread().getName()+" for + " + nodeName + " terminated",cause);
                            try {
                                socket.close();
                            } catch (IOException e) {
                                // ignore
                            }
                        }
                    });
                return computer.getChannel();
            } catch (AbortException e) {
                logw.println(e.getMessage());
                logw.println("Failed to establish the connection with the slave");
                throw e;
            } catch (IOException e) {
                logw.println("Failed to establish the connection with the slave " + nodeName);
                e.printStackTrace(logw);
                throw e;
            }
        }