public InputStream stream(final String host, final ByteBuffer message) throws IOException {
        ServerAddress serverAddress = host.contains(":") ? new ServerAddress(host) : new ServerAddress(host, defaultPort);
        Socket socket = sslContext.getSocketFactory().createSocket();

        try {
            socket.setSoTimeout(timeoutMillis);
            socket.connect(serverAddress.getSocketAddress(), timeoutMillis);
        } catch (IOException e) {
            closeSocket(socket);
            throw e;
        }

        try {
            OutputStream outputStream = socket.getOutputStream();

            byte[] bytes = new byte[message.remaining()];

            message.get(bytes);
            outputStream.write(bytes);
        } catch (IOException e) {
            closeSocket(socket);
            throw e;
        }

        try {
            return socket.getInputStream();
        } catch (IOException e) {
            closeSocket(socket);
            throw e;
        }
    }