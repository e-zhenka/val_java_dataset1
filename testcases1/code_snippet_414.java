public ResourceInvoker match(HttpRequest request, int start)
   {
      if (!CACHE || (request.getHttpHeaders().getMediaType() !=null && !request.getHttpHeaders().getMediaType().getParameters().isEmpty())) {
         return root.match(request, start).invoker;
      }
      MatchCache.Key key = new MatchCache.Key(request, start);
      MatchCache match = cache.get(key);
      if (match != null) {
         //System.out.println("*** cache hit: " + key.method + " " + key.path);
         request.setAttribute(RESTEASY_CHOSEN_ACCEPT, match.chosen);
      } else {
         match = root.match(request, start);
         if (match.match != null && match.match.expression.getNumGroups() == 0 && match.invoker instanceof ResourceMethodInvoker) {
            //System.out.println("*** caching: " + key.method + " " + key.path);
            match.match = null;
            if (cache.size() >= CACHE_SIZE) {
               cache.clear();
            }
            cache.putIfAbsent(key, match);
         }
      }
      return match.invoker;
   }