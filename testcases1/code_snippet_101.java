public static boolean isCsrfTokenValid(VaadinSession session,
            String requestToken) {

        if (session.getService().getDeploymentConfiguration()
                .isXsrfProtectionEnabled()) {
            String sessionToken = session.getCsrfToken();

            try {
                if (sessionToken == null || !MessageDigest.isEqual(
                        sessionToken.getBytes("UTF-8"),
                        requestToken.getBytes("UTF-8"))) {
                    return false;
                }
            } catch (UnsupportedEncodingException e) {
                getLogger().log(Level.WARNING,
                        "Session token was not UTF-8, this should never happen.");
                return false;
            }
        }
        return true;
    }