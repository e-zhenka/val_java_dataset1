protected boolean bindUser(DirContext context, String dn, String password) throws NamingException {
        boolean isValid = false;

        if (log.isDebugEnabled()) {
            log.debug("Binding the user.");
        }
        context.addToEnvironment(Context.SECURITY_AUTHENTICATION, "simple");
        context.addToEnvironment(Context.SECURITY_PRINCIPAL, dn);
        context.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
        try {
            context.getAttributes("", null);
            isValid = true;
            if (log.isDebugEnabled()) {
                log.debug("User " + dn + " successfully bound.");
            }
        } catch (AuthenticationException e) {
            isValid = false;
            if (log.isDebugEnabled()) {
                log.debug("Authentication failed for dn=" + dn);
            }
        }

        if (isLoginPropertySet(CONNECTION_USERNAME)) {
            context.addToEnvironment(Context.SECURITY_PRINCIPAL, getLDAPPropertyValue(CONNECTION_USERNAME));
        } else {
            context.removeFromEnvironment(Context.SECURITY_PRINCIPAL);
        }
        if (isLoginPropertySet(CONNECTION_PASSWORD)) {
            context.addToEnvironment(Context.SECURITY_CREDENTIALS, getLDAPPropertyValue(CONNECTION_PASSWORD));
        } else {
            context.removeFromEnvironment(Context.SECURITY_CREDENTIALS);
        }
        context.addToEnvironment(Context.SECURITY_AUTHENTICATION, getLDAPPropertyValue(AUTHENTICATION));
        return isValid;
    }