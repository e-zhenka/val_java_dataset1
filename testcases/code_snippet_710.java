@Override
      public Object getGroup(Object instance) {
         Object object;
         if (System.getSecurityManager() == null) {
            return invokeAccessibly(instance, method, Util.EMPTY_OBJECT_ARRAY);
         } else {
            return AccessController.doPrivileged((PrivilegedAction<Object>) () -> invokeAccessibly(instance, method, Util.EMPTY_OBJECT_ARRAY));
         }
      }