@Override
    public Object unmarshal(final Exchange exchange, final InputStream inputStream) throws Exception {
        final Hessian2Input in = new Hessian2Input(inputStream);
        try {
            in.startMessage();
            final Object obj = in.readObject();
            in.completeMessage();
            return obj;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }