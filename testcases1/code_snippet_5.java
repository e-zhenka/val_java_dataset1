protected void doDirectory(HttpServletRequest request,HttpServletResponse response, Resource resource)
        throws IOException
    {
        if (_directory)
        {
            String listing = resource.getListHTML(request.getRequestURI(),request.getPathInfo().lastIndexOf("/") > 0, request.getQueryString());
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().println(listing);
        }
        else
            response.sendError(HttpStatus.FORBIDDEN_403);
    }