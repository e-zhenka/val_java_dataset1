protected void sendWindowHandlerHtml(ExternalContext externalContext, String windowId)
    {
        HttpServletResponse httpResponse = (HttpServletResponse) externalContext.getResponse();

        try
        {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            httpResponse.setContentType("text/html");

            String windowHandlerHtml = clientWindowConfig.getClientWindowHtml();

            if (windowId == null)
            {
                windowId = UNINITIALIZED_WINDOW_ID_VALUE;
            }

            // set the windowId value in the javascript code
            windowHandlerHtml = windowHandlerHtml.replace(WINDOW_ID_REPLACE_PATTERN,
                                                          org.owasp.encoder.Encode.forJavaScriptBlock(windowId));
            // set the current request url
            // on the client we can't use window.location as the location
            // could be a different when using forwards
            windowHandlerHtml = windowHandlerHtml.replace(REQUEST_URL_REPLACE_PATTERN,
                                                          org.owasp.encoder.Encode.forJavaScriptBlock(
                                                              ClientWindowHelper.constructRequestUrl(externalContext)));
            // set the noscript-URL for users with no JavaScript
            windowHandlerHtml =
                windowHandlerHtml.replace(NOSCRIPT_URL_REPLACE_PATTERN,
                                          org.owasp.encoder.Encode.forHtmlAttribute(getNoscriptUrl(externalContext)));

            OutputStream os = httpResponse.getOutputStream();
            try
            {
                os.write(windowHandlerHtml.getBytes());
            }
            finally
            {
                os.close();
            }
        }
        catch (IOException ioe)
        {
            throw new FacesException(ioe);
        }
    }