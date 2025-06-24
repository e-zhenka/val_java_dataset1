@Override
  public void handle(RoutingContext context) {
    HttpServerRequest request = context.request();
    if (request.method() != HttpMethod.GET && request.method() != HttpMethod.HEAD) {
      if (log.isTraceEnabled()) log.trace("Not GET or HEAD so ignoring request");
      context.next();
    } else {
      String path = Utils.removeDots(Utils.urlDecode(context.normalisedPath(), false));
      // if the normalized path is null it cannot be resolved
      if (path == null) {
        log.warn("Invalid path: " + context.request().path());
        context.next();
        return;
      }

      // only root is known for sure to be a directory. all other directories must be identified as such.
      if (!directoryListing && "/".equals(path)) {
        path = indexPage;
      }

      // can be called recursive for index pages
      sendStatic(context, path);

    }
  }