@Override
      public Object getGroup(Object instance) {
         if (System.getSecurityManager() == null) {
            method.setAccessible(true);
         } else {
            AccessController.doPrivileged((PrivilegedAction<List<Method>>) () -> {
               method.setAccessible(true);
               return null;
            });
         }
         return invokeMethod(instance, method, Util.EMPTY_OBJECT_ARRAY);
      }