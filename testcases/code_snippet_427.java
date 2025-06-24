protected boolean isRestrictedClass(Object o) {
    if (o == null) {
      return false;
    }

    return (
      (
        o.getClass().getPackage() != null &&
        o.getClass().getPackage().getName().startsWith("java.lang.reflect")
      ) ||
      o instanceof Class ||
      o instanceof ClassLoader ||
      o instanceof Thread ||
      o instanceof Method ||
      o instanceof Field ||
      o instanceof Constructor
    );
  }