@Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException
    {
        super.encodeBegin(context, component);

        lazyInit();

        ClientWindowConfig.ClientWindowRenderMode clientWindowRenderMode =
                clientWindowConfig.getClientWindowRenderMode(context);

        // see DELTASPIKE-1113
        boolean delegatedWindowMode =
            ClientWindowConfig.ClientWindowRenderMode.DELEGATED.equals(clientWindowRenderMode);
        if (delegatedWindowMode)
        {
            return;
        }

        String windowId = clientWindow.getWindowId(context);
        // just to get sure if a user provides a own client window
        windowId = secureWindowId(windowId);

        ResponseWriter writer = context.getResponseWriter();
        writer.write("<script type=\"text/javascript\">");
        writer.write("(function(){");
        writer.write("dswh.init('");
        writer.writeText(windowId, null);
        writer.write("','"
                + clientWindowRenderMode.name() + "',"
                + maxWindowIdLength + ",{");

        writer.write("'tokenizedRedirect':" + clientWindowConfig.isClientWindowTokenizedRedirectEnabled());
        writer.write(",'storeWindowTreeOnLinkClick':"
                + clientWindowConfig.isClientWindowStoreWindowTreeEnabledOnLinkClick());
        writer.write(",'storeWindowTreeOnButtonClick':"
                + clientWindowConfig.isClientWindowStoreWindowTreeEnabledOnButtonClick());

        // see #729
        if (clientWindow.isInitialRedirectSupported(context))
        {
            Object cookie = ClientWindowHelper.getRequestWindowIdCookie(context, windowId);
            if (cookie != null && cookie instanceof Cookie)
            {
                Cookie servletCookie = (Cookie) cookie;
                writer.write(",'initialRedirectWindowId':'" + secureWindowId(servletCookie.getValue()) + "'");
                // expire/remove cookie
                servletCookie.setMaxAge(0);
                ((HttpServletResponse) context.getExternalContext().getResponse()).addCookie(servletCookie);
            }
        }

        writer.write("});");
        writer.write("})();");
        writer.write("</script>");
    }