private void checkRestrictedClass(Object o, Object method) {
    if (o instanceof Class || o instanceof ClassLoader || o instanceof Thread) {
      throw new MethodNotFoundException(
        "Cannot find method '" + method + "' in " + o.getClass()
      );
    }
  }