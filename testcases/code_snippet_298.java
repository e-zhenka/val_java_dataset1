public static boolean isCsrfTokenValid(VaadinSession session,
            String requestToken) {

        if (session.getService().getDeploymentConfiguration()
                .isXsrfProtectionEnabled()) {
            String sessionToken = session.getCsrfToken();

            if (sessionToken == null || !sessionToken.equals(requestToken)) {
                return false;
            }
        }
        return true;
    }