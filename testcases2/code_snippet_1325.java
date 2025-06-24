@Override
    protected synchronized String issueCrumb(ServletRequest request, String salt) {
        if (request instanceof HttpServletRequest) {
            if (md != null) {
                HttpServletRequest req = (HttpServletRequest) request;
                StringBuilder buffer = new StringBuilder();
                Authentication a = Jenkins.getAuthentication();
                buffer.append(a.getName());
                buffer.append(';');
                if (!isExcludeClientIPFromCrumb()) {
                    buffer.append(getClientIP(req));
                }
                if (!EXCLUDE_SESSION_ID) {
                    buffer.append(';');
                    buffer.append(getSessionId(req));
                }

                md.update(buffer.toString().getBytes());
                return Util.toHexString(md.digest(salt.getBytes()));
            }
        }
        return null;
    }