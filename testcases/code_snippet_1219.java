protected void setupSecurity() {
        if (securityMapper == null) {
            return;
        }

        addPermission(AnyTypePermission.ANY);
        denyTypes(new String[]{
            "java.beans.EventHandler", //
            "java.lang.ProcessBuilder", //
            "javax.imageio.ImageIO$ContainsFilter", //
            "jdk.nashorn.internal.objects.NativeString" });
        denyTypesByRegExp(new Pattern[]{LAZY_ITERATORS, JAVAX_CRYPTO, JAXWS_FILE_STREAM});
        allowTypeHierarchy(Exception.class);
        securityInitialized = false;
    }