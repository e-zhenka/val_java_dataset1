@Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String effectiveUser = request.getRemoteUser();
    if (securityEnabled) {
      try {
        // As Thrift HTTP transport doesn't support SPNEGO yet (THRIFT-889),
        // Kerberos authentication is being done at servlet level.
        final RemoteUserIdentity identity = doKerberosAuth(request);
        effectiveUser = identity.principal;
        // It is standard for client applications expect this header.
        // Please see http://tools.ietf.org/html/rfc4559 for more details.
        response.addHeader(WWW_AUTHENTICATE,  NEGOTIATE + " " + identity.outToken);
      } catch (HttpAuthenticationException e) {
        LOG.error("Kerberos Authentication failed", e);
        // Send a 401 to the client
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.addHeader(WWW_AUTHENTICATE, NEGOTIATE);
        response.getWriter().println("Authentication Error: " + e.getMessage());
        return;
      }
    }
    String doAsUserFromQuery = request.getHeader("doAs");
    if(effectiveUser == null) {
      effectiveUser = realUser.getShortUserName();
    }
    if (doAsUserFromQuery != null) {
      if (!doAsEnabled) {
        throw new ServletException("Support for proxyuser is not configured");
      }
      // The authenticated remote user is attempting to perform 'doAs' proxy user.
      UserGroupInformation remoteUser = UserGroupInformation.createRemoteUser(effectiveUser);
      // create and attempt to authorize a proxy user (the client is attempting
      // to do proxy user)
      UserGroupInformation ugi = UserGroupInformation.createProxyUser(doAsUserFromQuery,
          remoteUser);
      // validate the proxy user authorization
      try {
        ProxyUsers.authorize(ugi, request.getRemoteAddr(), conf);
      } catch (AuthorizationException e) {
        throw new ServletException(e.getMessage());
      }
      effectiveUser = doAsUserFromQuery;
    }
    hbaseHandler.setEffectiveUser(effectiveUser);
    super.doPost(request, response);
  }