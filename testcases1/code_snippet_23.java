public void resetAuthenticationFailureCounter(String username)
    {
        if (this.authorizationManager.hasAccess(Right.PROGRAM)) {
            this.authenticationFailureManager.resetAuthenticationFailureCounter(username);
        }
    }