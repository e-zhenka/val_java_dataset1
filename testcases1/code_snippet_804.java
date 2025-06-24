public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getParameterTypes().length == 0)
            return null;

        if (!ObjectName.class.isAssignableFrom(method.getParameterTypes()[0]))
            return null;

        MBeanServer mbs = (MBeanServer) proxy;
        if (mbs != null && Proxy.getInvocationHandler(mbs) instanceof MBeanInvocationHandler) {
            mbs = ((MBeanInvocationHandler) Proxy.getInvocationHandler(mbs)).getDelegate();
        }
        if (mbs instanceof EventAdminMBeanServerWrapper) {
            mbs = ((EventAdminMBeanServerWrapper) mbs).getDelegate();
        }

        ObjectName objectName = (ObjectName) args[0];
        if ("getAttribute".equals(method.getName())) {
            handleGetAttribute(mbs, objectName, (String) args[1]);
        } else if ("getAttributes".equals(method.getName())) {
            handleGetAttributes(mbs, objectName, (String[]) args[1]);
        } else if ("setAttribute".equals(method.getName())) {
            handleSetAttribute(mbs, objectName, (Attribute) args[1]);
        } else if ("setAttributes".equals(method.getName())) {
            handleSetAttributes(mbs, objectName, (AttributeList) args[1]);
        } else if ("invoke".equals(method.getName())) {
            handleInvoke(objectName, (String) args[1], (Object[]) args[2], (String[]) args[3]);
        }

        return null;
    }