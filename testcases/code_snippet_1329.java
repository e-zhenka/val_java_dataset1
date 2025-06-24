@Override
    protected synchronized String issueCrumb(ServletRequest request, String salt) {
        if (request instanceof HttpServletRequest) {
            if (md != null) {
                HttpServletRequest req = (HttpServletRequest) request;
                StringBuilder buffer = new StringBuilder();
                Authentication a = Jenkins.getAuthentication();
                if (a != null) {
                    buffer.append(a.getName());
                }
                buffer.append(';');
                if (!isExcludeClientIPFromCrumb()) {
                    buffer.append(getClientIP(req));
                }

                md.update(buffer.toString().getBytes());
                return Util.toHexString(md.digest(salt.getBytes()));
            }
        }
        return null;
    }