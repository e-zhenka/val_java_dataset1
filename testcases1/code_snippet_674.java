@Override
    public boolean validateCrumb(ServletRequest request, String salt, String crumb) {
        if (request instanceof HttpServletRequest) {
            String newCrumb = issueCrumb(request, salt);
            if ((newCrumb != null) && (crumb != null)) {
                return newCrumb.equals(crumb);
            }
        }
        return false;
    }