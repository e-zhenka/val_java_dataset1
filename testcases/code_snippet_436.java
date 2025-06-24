public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        LOGGER.entering(HudsonFilter.class.getName(), "doFilter");

        // this is not the best place to do it, but doing it here makes the patch smaller.
        ((HttpServletResponse)response).setHeader("X-Content-Type-Options", "nosniff");

        // to deal with concurrency, we need to capture the object.
        Filter f = filter;

        if(f==null) {
            // Hudson is starting up.
            chain.doFilter(request,response);
        } else {
            f.doFilter(request,response,chain);
        }
    }