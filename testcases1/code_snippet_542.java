@Override
    public String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        Object redirectAttribute = request.getAttribute(URI_OVERRIDE_ATTRIBUTE);
        String redirectFormParam = request.getParameter(FORM_REDIRECT_PARAMETER);
        if (redirectAttribute !=null) {
            logger.debug("Returning redirectAttribute saved URI:"+redirectAttribute);
            return (String) redirectAttribute;
        } else if (isApprovedFormRedirectUri(request, redirectFormParam)) {
            return redirectFormParam;
        } else {
            return super.determineTargetUrl(request, response);
        }
    }