protected void setupSecurity() {
        if (securityMapper == null) {
            return;
        }

        addPermission(AnyTypePermission.ANY);
        denyTypes(new String[]{"java.beans.EventHandler", "javax.imageio.ImageIO$ContainsFilter"});
        denyTypesByRegExp(new Pattern[]{LAZY_ITERATORS, JAVAX_CRYPTO});
        allowTypeHierarchy(Exception.class);
        securityInitialized = false;
    }