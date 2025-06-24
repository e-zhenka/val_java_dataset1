public void doFilter(ServletRequest request, ServletResponse res, FilterChain chain)
          throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String uri;
        if (req.getPathInfo() == null) {
            // workaround: on some containers such as CloudBees DEV@cloud, req.getPathInfo() is unexpectedly null,
            // construct pathInfo based on contextPath and requestUri
            uri = req.getRequestURI().substring(req.getContextPath().length());
        } else {
            uri = req.getPathInfo();
        }
        if (uriPattern != null && uriPattern.matcher(uri).matches()) {
            User user = User.current();
            String username = user != null ? user.getId() : req.getRemoteAddr();
            String extra = "";
            // For queue items, show what task is in the queue:
            if (uri.startsWith("/queue/item/")) {
                extra = extractInfoFromQueueItem(uri);
            } else if (uri.startsWith("/queue/cancelItem")) {
                extra = getFormattedQueueItemUrlFromItemId(Integer.parseInt(req.getParameter("id")));
                // not sure of the intent of the original author
                // it looks to me we should always log the query parameters
                // could we leak sensitive data?  There shouldn't be any in a query parameter...except for a badly coded plugin
                // let's see if this becomes a wanted feature...
                uri += "?" + req.getQueryString();
            }

            if (LOGGER.isLoggable(Level.FINE))
                LOGGER.log(Level.FINE, "Audit request {0} by user {1}", new Object[]{uri, username});

            onRequest(uri, extra, username);
        } else {
            LOGGER.log(Level.FINEST, "Skip audit for request {0}", uri);
        }
        chain.doFilter(req, res);
    }