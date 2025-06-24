public static boolean isCsrfTokenValid(VaadinSession session,
            String requestToken) {

        if (session.getService().getDeploymentConfiguration()
                .isXsrfProtectionEnabled()) {
            String sessionToken = session.getCsrfToken();

            if (uiToken == null || !MessageDigest.isEqual(
                    uiToken.getBytes(StandardCharsets.UTF_8),
                    requestToken.getBytes(StandardCharsets.UTF_8))) {
                return false;
            }
        }
        return true;
    }