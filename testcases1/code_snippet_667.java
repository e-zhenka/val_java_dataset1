public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        LOGGER.entering(HudsonFilter.class.getName(), "doFilter");
        
        // to deal with concurrency, we need to capture the object.
        Filter f = filter;

        if(f==null) {
            // Hudson is starting up.
            chain.doFilter(request,response);
        } else {
            f.doFilter(request,response,chain);
        }
    }