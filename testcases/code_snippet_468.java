protected void throwable(BodyWriter w, Throwable throwable, boolean isCause) {
    if (throwable != null) {
      if (isCause) {
        w.escape("Caused by: ");
      }

      w.escapeln(throwable.toString());
      for (StackTraceElement ste : throwable.getStackTrace()) {
        String className = ste.getClassName();
        if (className.startsWith("ratpack")
          || className.startsWith("io.netty")
          || className.startsWith("com.google")
          || className.startsWith("java")
          || className.startsWith("org.springsource.loaded")
          ) {
          w.print("<span class='stack-core'>  at ").escape(ste.toString()).println("</span>");
        } else {
          w.print("  at ").escape(ste.toString()).println("");
        }
      }

      throwable(w, throwable.getCause(), true);
    }
  }