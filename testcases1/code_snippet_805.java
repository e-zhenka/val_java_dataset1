@Override
  public void handle(RoutingContext ctx) {

    if (nagHttps) {
      String uri = ctx.request().absoluteURI();
      if (uri != null && !uri.startsWith("https:")) {
        log.warn("Using session cookies without https could make you susceptible to session hijacking: " + uri);
      }
    }

    HttpMethod method = ctx.request().method();

    switch (method) {
      case GET:
        final String token = generateToken();
        // put the token in the context for users who prefer to render the token directly on the HTML
        ctx.put(headerName, token);
        ctx.addCookie(Cookie.cookie(cookieName, token).setPath(cookiePath));
        ctx.next();
        break;
      case POST:
      case PUT:
      case DELETE:
      case PATCH:
        final String header = ctx.request().getHeader(headerName);
        if (validateToken(header == null ? ctx.request().getFormAttribute(headerName) : header)) {
          ctx.next();
        } else {
          forbidden(ctx);
        }
        break;
      default:
        // ignore these methods
        ctx.next();
        break;
    }
  }