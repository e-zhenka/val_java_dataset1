public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        CrumbIssuer crumbIssuer = getCrumbIssuer();
        if (crumbIssuer == null || !(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if ("POST".equals(httpRequest.getMethod())) {
            for (CrumbExclusion e : CrumbExclusion.all()) {
                if (e.process(httpRequest,httpResponse,chain))
                    return;
            }

            String crumbFieldName = crumbIssuer.getDescriptor().getCrumbRequestField();
            String crumbSalt = crumbIssuer.getDescriptor().getCrumbSalt();

            String crumb = httpRequest.getHeader(crumbFieldName);
            boolean valid = false;
            if (crumb == null) {
                Enumeration<?> paramNames = request.getParameterNames();
                while (paramNames.hasMoreElements()) {
                    String paramName = (String) paramNames.nextElement();
                    if (crumbFieldName.equals(paramName)) {
                        crumb = request.getParameter(paramName);
                        break;
                    }
                }
            }
            if (crumb != null) {
                if (crumbIssuer.validateCrumb(httpRequest, crumbSalt, crumb)) {
                    valid = true;
                } else {
                    LOGGER.log(Level.WARNING, "Found invalid crumb {0}.  Will check remaining parameters for a valid one...", crumb);
                }
            }

            if (valid) {
                chain.doFilter(request, response);
            } else {
                LOGGER.log(Level.WARNING, "No valid crumb was included in request for {0}. Returning {1}.", new Object[] {httpRequest.getRequestURI(), HttpServletResponse.SC_FORBIDDEN});
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN,"No valid crumb was included in the request");
            }
        } else {
            chain.doFilter(request, response);
        }
    }