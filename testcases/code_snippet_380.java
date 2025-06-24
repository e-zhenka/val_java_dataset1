protected void traceLdapEnv(Properties env)
   {
      if (trace)
      {
         Properties tmp = new Properties();
         tmp.putAll(env);
         String credentials = tmp.getProperty(Context.SECURITY_CREDENTIALS);
         String bindCredential = tmp.getProperty(BIND_CREDENTIAL);
         
         if (credentials != null && credentials.length() > 0) {
        	 tmp.setProperty(Context.SECURITY_CREDENTIALS, "***");
         }
            
         if (bindCredential != null && bindCredential.length() > 0) {
        	 tmp.setProperty(BIND_CREDENTIAL, "***");
         }
         
         log.trace("Logging into LDAP server, env=" + tmp.toString());
      }
   }