public void handleMessage(Message message) {
        Exchange ex = message.getExchange();
        BindingOperationInfo binding = ex.get(BindingOperationInfo.class);
        if (null != binding && null != binding.getOperationInfo() && binding.getOperationInfo().isOneWay()) {
            closeInput(message);
            return;
        }
        Message out = ex.getOutMessage();
        if (out != null) {
            getBackChannelConduit(message);
            if (binding != null) {
                out.put(MessageInfo.class, binding.getOperationInfo().getOutput());
                out.put(BindingMessageInfo.class, binding.getOutput());
            }
            
            InterceptorChain outChain = out.getInterceptorChain();
            if (outChain == null) {
                outChain = OutgoingChainInterceptor.getChain(ex, chainCache);
                out.setInterceptorChain(outChain);
            }
            outChain.doIntercept(out);
        }
    }