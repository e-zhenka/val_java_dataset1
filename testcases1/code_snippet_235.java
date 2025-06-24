private void setupJackson(Injector injector, final ObjectMapper mapper)
  {
    final GuiceAnnotationIntrospector guiceIntrospector = new GuiceAnnotationIntrospector();

    mapper.setInjectableValues(new GuiceInjectableValues(injector));
    mapper.setAnnotationIntrospectors(
        new AnnotationIntrospectorPair(guiceIntrospector, mapper.getSerializationConfig().getAnnotationIntrospector()),
        new AnnotationIntrospectorPair(guiceIntrospector, mapper.getDeserializationConfig().getAnnotationIntrospector())
    );
  }