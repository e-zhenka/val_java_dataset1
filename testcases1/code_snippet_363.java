private static boolean validateGlobalResourceAccess(String globalName) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        while (cl != null) {
            Map<String,String> registrations = globalResourceRegistrations.get(cl);
            if (registrations != null && registrations.containsValue(globalName)) {
                return true;
            }
            cl = cl.getParent();
        }
        return false;
    }