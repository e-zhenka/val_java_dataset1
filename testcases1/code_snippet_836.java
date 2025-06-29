public void handleMessage(Message message) {
        Exchange ex = message.getExchange();
        BindingOperationInfo binding = ex.get(BindingOperationInfo.class);
        //if we get this far, we're going to be outputting some valid content, but we COULD
        //also be "echoing" some of the content from the input.   Thus, we need to 
        //mark it as requiring the input to be cached.   
        message.put("cxf.io.cacheinput", Boolean.TRUE);
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