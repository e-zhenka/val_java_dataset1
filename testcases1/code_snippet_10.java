@Override
    public String getWindowId(FacesContext facesContext)
    {
        Map<String, Object> requestMap = facesContext.getExternalContext().getRequestMap();

        // try to lookup from cache
        String windowId = (String) requestMap.get(CACHE_WINDOW_ID);
        if (windowId != null)
        {
            return windowId;
        }

        windowId = getOrCreateWindowId(facesContext);


        if (windowId != null)
        {
            windowId = sanitiseWindowId(windowId);

            // don't cut the windowId generated from JSF
            ClientWindowConfig.ClientWindowRenderMode clientWindowRenderMode =
                    clientWindowConfig.getClientWindowRenderMode(facesContext);
            if (!ClientWindowConfig.ClientWindowRenderMode.DELEGATED.equals(clientWindowRenderMode))
            {
                if (windowId.length() > this.maxWindowIdCount)
                {
                    windowId = windowId.substring(0, this.maxWindowIdCount);
                }
            }

            requestMap.put(CACHE_WINDOW_ID, windowId);
        }

        return windowId;
    }