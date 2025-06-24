private static boolean validateGlobalResourceAccess(String globalName) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Map<String,String> registrations = globalResourceRegistrations.get(cl);
        if (registrations != null && registrations.containsValue(globalName)) {
            return true;
        }
        return false;
    }