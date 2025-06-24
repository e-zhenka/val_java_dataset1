@Override
    public boolean validateCrumb(ServletRequest request, String salt, String crumb) {
        if (request instanceof HttpServletRequest) {
            String newCrumb = issueCrumb(request, salt);
            if ((newCrumb != null) && (crumb != null)) {
                return MessageDigest.isEqual(newCrumb.getBytes(), crumb.getBytes());
            }
        }
        return false;
    }