@Override
  public void handle(RoutingContext ctx) {

    if (nagHttps) {
      String uri = ctx.request().absoluteURI();
      if (uri != null && !uri.startsWith("https:")) {
        log.trace("Using session cookies without https could make you susceptible to session hijacking: " + uri);
      }
    }

    HttpMethod method = ctx.request().method();
    Session session = ctx.session();

    // if we're being strict with the origin
    // ensure that they are always valid
    if (!isValidOrigin(ctx)) {
      ctx.fail(403);
      return;
    }

    switch (method.name()) {
      case "GET":
        final String token;

        if (session == null) {
          // if there's no session to store values, tokens are issued on every request
          token = generateAndStoreToken(ctx);
        } else {
          // get the token from the session, this also considers the fact
          // that the token might be invalid as it was issued for a previous session id
          // session id's change on session upgrades (unauthenticated -> authenticated; role change; etc...)
          String sessionToken = getTokenFromSession(ctx);
          // when there's no token in the session, then we behave just like when there is no session
          // create a new token, but we also store it in the session for the next runs
          if (sessionToken == null) {
            token = generateAndStoreToken(ctx);
            // storing will include the session id too. The reason is that if a session is upgraded
            // we don't want to allow the token to be valid anymore
            session.put(headerName, session.id() + "/" + token);
          } else {
            String[] parts = sessionToken.split("\\.");
            final long ts = parseLong(parts[1]);

            if (ts == -1) {
              // fallback as the token is expired
              token = generateAndStoreToken(ctx);
            } else {
              if (!(System.currentTimeMillis() > ts + timeout)) {
                // we're still on the same session, no need to regenerate the token
                // also note that the token isn't expired, so it can be reused
                token = sessionToken;
                // in this case specifically we don't issue the token as it is unchanged
                // the user agent still has it from the previous interaction.
              } else {
                // fallback as the token is expired
                token = generateAndStoreToken(ctx);
              }
            }
          }
        }
        // put the token in the context for users who prefer to render the token directly on the HTML
        ctx.put(headerName, token);
        ctx.next();
        break;
      case "POST":
      case "PUT":
      case "DELETE":
      case "PATCH":
        if (isValidRequest(ctx)) {
          // it matches, so refresh the token to avoid replay attacks
          token = generateAndStoreToken(ctx);
          // put the token in the context for users who prefer to
          // render the token directly on the HTML
          ctx.put(headerName, token);
          ctx.next();
        } else {
          ctx.fail(403);
        }
        break;
      default:
        // ignore other methods
        ctx.next();
        break;
    }
  }