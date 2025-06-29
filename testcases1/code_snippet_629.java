@Override
        public void doFilter(final ServletRequest req, final ServletResponse resp, final FilterChain filterChain)
                throws IOException, ServletException {

            // set frame options accordingly
            final HttpServletResponse response = (HttpServletResponse) resp;
            response.addHeader(FRAME_OPTIONS, SAME_ORIGIN);

            filterChain.doFilter(req, resp);
        }