protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
        Object object = super.getHandlerInternal(request);

        if (object instanceof String) {
            String handlerName = (String) object;
            object = getApplicationContext().getBean(handlerName);
        }
        if (object instanceof HandlerExecutionChain) {
            HandlerExecutionChain handlerExecutionChain = (HandlerExecutionChain) object;
            object = handlerExecutionChain.getHandler();
        }

        if (object != null) {
            ServletRequestDataBinder binder = new ServletRequestDataBinder(object, "request");
            try {
                binder.bind(request);
                binder.setIgnoreUnknownFields(true);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Bound POJO is now: " + object);
                }
            }
            catch (Exception e) {
                LOG.warn("Caught: " + e, e);
                throw e;
            }
        }
        return object;
    }