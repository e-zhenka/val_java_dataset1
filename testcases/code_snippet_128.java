protected boolean bindUser(DirContext context, String dn, String password) throws NamingException {
      boolean isValid = false;

      if (logger.isDebugEnabled()) {
         logger.debug("Binding the user.");
      }
      context.addToEnvironment(Context.SECURITY_PRINCIPAL, dn);
      context.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
      try {
         context.getAttributes("", null);
         isValid = true;
         if (logger.isDebugEnabled()) {
            logger.debug("User " + dn + " successfully bound.");
         }
      } catch (AuthenticationException e) {
         isValid = false;
         if (logger.isDebugEnabled()) {
            logger.debug("Authentication failed for dn=" + dn);
         }
      }

      if (isLoginPropertySet(CONNECTION_USERNAME)) {
         context.addToEnvironment(Context.SECURITY_PRINCIPAL, getLDAPPropertyValue(CONNECTION_USERNAME));
      } else {
         context.removeFromEnvironment(Context.SECURITY_PRINCIPAL);
      }
      if (isLoginPropertySet(CONNECTION_PASSWORD)) {
         context.addToEnvironment(Context.SECURITY_CREDENTIALS, getPlainPassword(getLDAPPropertyValue(CONNECTION_PASSWORD)));
      } else {
         context.removeFromEnvironment(Context.SECURITY_CREDENTIALS);
      }

      return isValid;
   }