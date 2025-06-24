@Override
    public void setAttribute(String name, Object value) {

        // Name cannot be null
        if (name == null)
            throw new IllegalArgumentException
                (sm.getString("coyoteRequest.setAttribute.namenull"));

        // Null value is the same as removeAttribute()
        if (value == null) {
            removeAttribute(name);
            return;
        }

        if (name.equals(Globals.DISPATCHER_TYPE_ATTR)) {
            internalDispatcherType = (DispatcherType)value;
            return;
        } else if (name.equals(Globals.DISPATCHER_REQUEST_PATH_ATTR)) {
            requestDispatcherPath = value;
            return;
        }
        
        if (name.equals(Globals.ASYNC_SUPPORTED_ATTR)) {
            this.asyncSupported = (Boolean)value;
        }

        Object oldValue = null;
        boolean replaced = false;

        // Add or replace the specified attribute
        // Check for read only attribute
        // requests are per thread so synchronization unnecessary
        if (readOnlyAttributes.containsKey(name)) {
            return;
        }

        oldValue = attributes.put(name, value);
        if (oldValue != null) {
            replaced = true;
        }

        // Pass special attributes to the native layer
        if (name.startsWith("org.apache.tomcat.")) {
            coyoteRequest.setAttribute(name, value);
        }
        
        // Notify interested application event listeners
        Object listeners[] = context.getApplicationEventListeners();
        if ((listeners == null) || (listeners.length == 0))
            return;
        ServletRequestAttributeEvent event = null;
        if (replaced)
            event =
                new ServletRequestAttributeEvent(context.getServletContext(),
                                                 getRequest(), name, oldValue);
        else
            event =
                new ServletRequestAttributeEvent(context.getServletContext(),
                                                 getRequest(), name, value);

        for (int i = 0; i < listeners.length; i++) {
            if (!(listeners[i] instanceof ServletRequestAttributeListener))
                continue;
            ServletRequestAttributeListener listener =
                (ServletRequestAttributeListener) listeners[i];
            try {
                if (replaced) {
                    listener.attributeReplaced(event);
                } else {
                    listener.attributeAdded(event);
                }
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                context.getLogger().error(sm.getString("coyoteRequest.attributeEvent"), t);
                // Error valve will pick this exception up and display it to user
                attributes.put(RequestDispatcher.ERROR_EXCEPTION, t );
            }
        }
    }