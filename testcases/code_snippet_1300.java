protected void error(HttpServletRequest request,
                         HttpServletResponse response,
                         Throwable e)
    {
        String path = ServletUtils.getPath(request);
        if (response.isCommitted())
        {
            getLog().error("An error occured but the response headers have already been sent.");
            getLog().error("Error processing a template for path '{}'", path, e);
            return;
        }

        try
        {
            getLog().error("Error processing a template for path '{}'", path, e);
            StringBuilder html = new StringBuilder();
            html.append("<html>\n");
            html.append("<head><title>Error</title></head>\n");
            html.append("<body>\n");
            html.append("<h2>VelocityView : Error processing a template for path '");
            html.append(path);
            html.append("'</h2>\n");

            Throwable cause = e;

            String why = cause.getMessage();
            if (why != null && why.length() > 0)
            {
                html.append(StringEscapeUtils.escapeHtml4(why));
                html.append("\n<br>\n");
            }

            //TODO: add line/column/template info for parse errors et al

            // if it's an MIE, i want the real stack trace!
            if (cause instanceof MethodInvocationException)
            {
                // get the real cause
                cause = cause.getCause();
            }

            StringWriter sw = new StringWriter();
            cause.printStackTrace(new PrintWriter(sw));

            html.append("<pre>\n");
            html.append(StringEscapeUtils.escapeHtml4(sw.toString()));
            html.append("</pre>\n");
            html.append("</body>\n");
            html.append("</html>");
            response.getWriter().write(html.toString());
        }
        catch (Exception e2)
        {
            // clearly something is quite wrong.
            // let's log the new exception then give up and
            // throw a runtime exception that wraps the first one
            String msg = "Exception while printing error screen";
            getLog().error(msg, e2);
            throw new RuntimeException(msg, e);
        }
    }