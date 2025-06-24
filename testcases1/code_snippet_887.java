@Override
  public User getUser() throws IllegalStateException {
    Organization org = getOrganization();
    if (org == null)
      throw new IllegalStateException("No organization is set in security context");

    User delegatedUser = delegatedUserHolder.get();

    if (delegatedUser != null) {
      return delegatedUser;
    }

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    JaxbOrganization jaxbOrganization = JaxbOrganization.fromOrganization(org);
    if (auth != null) {
      Object principal = auth.getPrincipal();
      if ((principal != null) && (principal instanceof UserDetails)) {
        UserDetails userDetails = (UserDetails) principal;

        User user = null;

        // If user exists, fetch it from the userDirectory
        if (userDirectory != null) {
          user = userDirectory.loadUser(userDetails.getUsername());
          if (user == null) {
            logger.debug(
                    "Authenticated user '{}' could not be found in any of the current UserProviders. Continuing anyway...",
                    userDetails.getUsername());
          }
        } else {
          logger.debug("No UserDirectory was found when trying to search for user '{}'", userDetails.getUsername());
        }

        // Add the roles (authorities) in the security context
        Set<JaxbRole> roles = new HashSet<JaxbRole>();
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        if (authorities != null) {
          for (GrantedAuthority ga : authorities) {
            roles.add(new JaxbRole(ga.getAuthority(), jaxbOrganization));
          }
        }

        if (user == null) {
          // No user was found. Create one to hold the auth information from the security context
          user = new JaxbUser(userDetails.getUsername(), null, jaxbOrganization, roles);
        } else {
          // Combine the existing user with the roles in the security context
          user = JaxbUser.fromUser(user, roles);
        }

        // Save the user to retrieve it quicker the next time(s) this method is called (by this thread)
        delegatedUserHolder.set(user);

        return user;
      }
    }

    // Return the anonymous user by default
    return SecurityUtil.createAnonymousUser(jaxbOrganization);
  }