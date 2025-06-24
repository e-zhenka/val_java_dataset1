public void handleMessage(Message message) throws Fault {
        if (isServerGet(message)) {
            return;
        }
        prepareMessage(message);
        message.getInterceptorChain().add(
              new StaxActionInInterceptor(requireSignature, requireEncryption));
    }