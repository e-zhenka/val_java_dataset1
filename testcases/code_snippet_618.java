public static boolean isCsrfTokenValid(VaadinSession session,
            String requestToken) {

        if (session.getService().getDeploymentConfiguration()
                .isXsrfProtectionEnabled()) {
            String sessionToken = session.getCsrfToken();

            if (sessionToken == null || !MessageDigest.isEqual(
                    sessionToken.getBytes(StandardCharsets.UTF_8),
                    requestToken.getBytes(StandardCharsets.UTF_8))) {
                return false;
            }
        }
        return true;
    }