@VisibleForTesting
  public static void setupJackson(Injector injector, final ObjectMapper mapper)
  {
    mapper.setInjectableValues(new GuiceInjectableValues(injector));
    setupAnnotationIntrospector(mapper, new GuiceAnnotationIntrospector());
  }