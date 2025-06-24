@Nullable
  SearchResult getLdapUserObject(BasicAuthLDAPConfig ldapConfig, DirContext context, String username)
  {
    try {
      SearchControls sc = new SearchControls();
      sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
      sc.setReturningAttributes(new String[] {ldapConfig.getUserAttribute(), "memberOf" });
      String encodedUsername = encodeForLDAP(username, true);
      NamingEnumeration<SearchResult> results = context.search(
          ldapConfig.getBaseDn(),
          StringUtils.format(ldapConfig.getUserSearch(), encodedUsername),
          sc);
      try {
        if (!results.hasMore()) {
          return null;
        }
        return results.next();
      }
      finally {
        results.close();
      }
    }
    catch (NamingException e) {
      LOG.debug(e, "Unable to find user '%s'", username);
      return null;
    }
  }