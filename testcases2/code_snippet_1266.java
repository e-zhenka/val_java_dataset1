protected void setupSecurity() {
        if (securityMapper == null) {
            return;
        }

        addPermission(AnyTypePermission.ANY);
        denyTypes(new String[]{
            "java.beans.EventHandler", //
            "java.lang.ProcessBuilder", //
            "javax.imageio.ImageIO$ContainsFilter", //
            "jdk.nashorn.internal.objects.NativeString", //
            "com.sun.corba.se.impl.activation.ServerTableEntry", //
            "com.sun.tools.javac.processing.JavacProcessingEnvironment$NameProcessIterator", //
            "sun.awt.datatransfer.DataTransferer$IndexOrderComparator", //
            "sun.swing.SwingLazyValue"});
        denyTypesByRegExp(new Pattern[]{
            LAZY_ITERATORS, GETTER_SETTER_REFLECTION, PRIVILEGED_GETTER, JAVAX_CRYPTO, JAXWS_ITERATORS,
            JAVAFX_OBSERVABLE_LIST__, BCEL_CL});
        denyTypeHierarchy(InputStream.class);
        denyTypeHierarchyDynamically("java.nio.channels.Channel");
        denyTypeHierarchyDynamically("javax.activation.DataSource");
        denyTypeHierarchyDynamically("javax.sql.rowset.BaseRowSet");
        allowTypeHierarchy(Exception.class);
        securityInitialized = false;
    }