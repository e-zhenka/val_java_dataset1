public synchronized void download(StaplerRequest req, StaplerResponse rsp) throws InterruptedException, IOException {
        rsp.setStatus(HttpServletResponse.SC_OK);

        // server->client channel.
        // this is created first, and this controls the lifespan of the channel
        rsp.addHeader("Transfer-Encoding", "chunked");
        OutputStream out = rsp.getOutputStream();
        if (DIY_CHUNKING) out = new ChunkedOutputStream(out);

        // send something out so that the client will see the HTTP headers
        out.write("Starting HTTP duplex channel".getBytes());
        out.flush();

        {// wait until we have the other channel
            long end = System.currentTimeMillis() + CONNECTION_TIMEOUT;
            while (upload == null && System.currentTimeMillis()<end)
                wait(1000);

            if (upload==null)
                throw new IOException("HTTP full-duplex channel timeout: "+uuid);
        }

        try {
            channel = new Channel("HTTP full-duplex channel " + uuid,
                    Computer.threadPoolForRemoting, Mode.BINARY, upload, out, null, restricted);

            // so that we can detect dead clients, periodically send something
            PingThread ping = new PingThread(channel) {
                @Override
                protected void onDead(Throwable diagnosis) {
                    LOGGER.log(Level.INFO,"Duplex-HTTP session " + uuid + " is terminated",diagnosis);
                    // this will cause the channel to abort and subsequently clean up
                    try {
                        upload.close();
                    } catch (IOException e) {
                        // this can never happen
                        throw new AssertionError(e);
                    }
                }

                @Override
                protected void onDead() {
                    onDead(null);
                }
            };
            ping.start();
            main(channel);
            channel.join();
            ping.interrupt();
        } finally {
            // publish that we are done
            completed=true;
            notify();
        }
    }