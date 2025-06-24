@Override
    public Object unmarshal(final Exchange exchange, final InputStream inputStream) throws Exception {
        final Hessian2Input in;
        if (!whitelistEnabled) {
            in = new Hessian2Input(inputStream);
        } else {
            HessianFactory factory = new HessianFactory();
            if (ObjectHelper.isNotEmpty(allowedUnmarshallObjects)) {
                factory.allow(allowedUnmarshallObjects);
            }
            if (ObjectHelper.isNotEmpty(deniedUnmarshallObjects)) {
                factory.deny(deniedUnmarshallObjects);
            }
            in = factory.createHessian2Input(inputStream);
        }
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