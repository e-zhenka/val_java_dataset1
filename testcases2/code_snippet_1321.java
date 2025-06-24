public void installConfigFiles(final boolean builtIn) {
        final File openejbCoreJar = paths.getOpenEJBCoreJar();
        final File confDir = paths.getCatalinaConfDir();
        final Alerts alerts = this.alerts;

        if (openejbCoreJar == null) {
            // the core jar contains the config files
            return;
        }
        final JarFile coreJar;
        try {
            coreJar = new JarFile(openejbCoreJar);
        } catch (final IOException e) {
            return;
        }

        //
        // conf/tomee.xml
        //
        final File openEjbXmlFile = new File(confDir, "tomee.xml");
        if (!openEjbXmlFile.exists()) {
            // read in the openejb.xml file from the openejb core jar
            final String openEjbXml = Installers.readEntry(coreJar, "default.openejb.conf", alerts);
            if (openEjbXml != null) {
                if (Installers.writeAll(openEjbXmlFile, openEjbXml.replace("<openejb>", "<tomee>").replace("</openejb>", "</tomee>"), alerts)) {
                    alerts.addInfo("Copy tomee.xml to conf");
                }
            }
        }


        //
        // conf/logging.properties
        // now we are using tomcat one of jdk one by default
        //
        final String openejbLoggingProps = "################################\r\n" +
                "# OpenEJB/TomEE specific loggers\r\n" +
                "################################\r\n" +
                "#\r\n" +
                "# ACTIVATE LEVEL/HANDLERS YOU WANT\r\n" +
                "# IF YOU ACTIVATE 5tomee.org.apache.juli.FileHandler\r\n" +
                "# ADD IT TO handlers LINE LIKE:\r\n" +
                "#\r\n" +
                "# handlers = 1catalina.org.apache.juli.FileHandler, 2localhost.org.apache.juli.FileHandler, 3manager.org.apache.juli.FileHandler, 4host-manager.org.apache.juli.FileHandler, 5tomee.org.apache.juli.FileHandler, java.util.logging.ConsoleHandler\r\n" +
                "#\r\n" +
                "# LEVELS:\r\n" +
                "# =======\r\n" +
                "#\r\n" +
                "# OpenEJB.level             = WARNING\r\n" +
                "# OpenEJB.options.level     = INFO\r\n" +
                "# OpenEJB.server.level      = INFO\r\n" +
                "# OpenEJB.startup.level     = INFO\r\n" +
                "# OpenEJB.startup.service.level = WARNING\r\n" +
                "# OpenEJB.startup.config.level = INFO\r\n" +
                "# OpenEJB.hsql.level        = INFO\r\n" +
                "# CORBA-Adapter.level       = WARNING\r\n" +
                "# Transaction.level         = WARNING\r\n" +
                "# org.apache.activemq.level = SEVERE\r\n" +
                "# org.apache.geronimo.level = SEVERE\r\n" +
                "# openjpa.level             = WARNING\r\n" +
                "# OpenEJB.cdi.level         = INFO\r\n" +
                "# org.apache.webbeans.level = INFO\r\n" +
                "# org.apache.openejb.level = FINE\r\n" +
                "#\r\n" +
                "# HANDLERS:\r\n" +
                "# =========\r\n" +
                "#\r\n" +
                "# OpenEJB.handlers             = 5tomee.org.apache.juli.FileHandler, java.util.logging.ConsoleHandler\r\n" +
                "# OpenEJB.options.handlers     = 5tomee.org.apache.juli.FileHandler, java.util.logging.ConsoleHandler\r\n" +
                "# OpenEJB.server.handlers      = 5tomee.org.apache.juli.FileHandler, java.util.logging.ConsoleHandler\r\n" +
                "# OpenEJB.startup.handlers     = 5tomee.org.apache.juli.FileHandler, java.util.logging.ConsoleHandler\r\n" +
                "# OpenEJB.startup.service.handlers = 5tomee.org.apache.juli.FileHandler, java.util.logging.ConsoleHandler\r\n" +
                "# OpenEJB.startup.config.handlers = 5tomee.org.apache.juli.FileHandler, java.util.logging.ConsoleHandler\r\n" +
                "# OpenEJB.hsql.handlers        = 5tomee.org.apache.juli.FileHandler, java.util.logging.ConsoleHandler\r\n" +
                "# CORBA-Adapter.handlers       = 5tomee.org.apache.juli.FileHandler, java.util.logging.ConsoleHandler\r\n" +
                "# Transaction.handlers         = 5tomee.org.apache.juli.FileHandler, java.util.logging.ConsoleHandler\r\n" +
                "# org.apache.activemq.handlers = 5tomee.org.apache.juli.FileHandler, java.util.logging.ConsoleHandler\r\n" +
                "# org.apache.geronimo.handlers = 5tomee.org.apache.juli.FileHandler, java.util.logging.ConsoleHandler\r\n" +
                "# openjpa.handlers             = 5tomee.org.apache.juli.FileHandler, java.util.logging.ConsoleHandler\r\n" +
                "# OpenEJB.cdi.handlers         = 5tomee.org.apache.juli.FileHandler, java.util.logging.ConsoleHandler\r\n" +
                "# org.apache.webbeans.handlers = 5tomee.org.apache.juli.FileHandler, java.util.logging.ConsoleHandler\r\n" +
                "# org.apache.openejb.handlers = 5tomee.org.apache.juli.FileHandler, java.util.logging.ConsoleHandler\r\n" +
                "#\r\n" +
                "# TOMEE HANDLER SAMPLE:\r\n" +
                "# =====================\r\n" +
                "#\r\n" +
                "# 5tomee.org.apache.juli.FileHandler.level = FINEST\r\n" +
                "# 5tomee.org.apache.juli.FileHandler.directory = ${catalina.base}/logs\r\n" +
                "# 5tomee.org.apache.juli.FileHandler.prefix = tomee.\r\n";
        final File loggingPropsFile = new File(confDir, "logging.properties");
        String newLoggingProps = null;
        if (!loggingPropsFile.exists()) {
            newLoggingProps = openejbLoggingProps;
        } else {
            final String loggingPropsOriginal = Installers.readAll(loggingPropsFile, alerts);
            if (!loggingPropsOriginal.toLowerCase().contains("openejb")) {
                // append our properties
                newLoggingProps = loggingPropsOriginal +
                        "\r\n\r\n" +
                        openejbLoggingProps + "\r\n";
            }
        }
        if (builtIn) {
            installTomEEJuli(alerts, loggingPropsFile, newLoggingProps);
        }

        final File openejbSystemProperties = new File(confDir, "system.properties");
        if (!openejbSystemProperties.exists()) {
            FileWriter systemPropertiesWriter = null;
            try {
                systemPropertiesWriter = new FileWriter(openejbSystemProperties);

                systemPropertiesWriter.write("# all this properties are added at JVM system properties at startup\n");
                systemPropertiesWriter.write("# here some default Apache TomEE system properties\n");
                systemPropertiesWriter.write("# for more information please see http://tomee.apache.org/properties-listing.html\n");

                systemPropertiesWriter.write("\n");
                systemPropertiesWriter.write("# openejb.check.classloader = false\n");
                systemPropertiesWriter.write("# openejb.check.classloader.verbose = false\n");

                systemPropertiesWriter.write("\n");
                systemPropertiesWriter.write("# tomee.jaxws.subcontext = webservices\n");
                systemPropertiesWriter.write("# tomee.jaxws.oldsubcontext = false\n");

                systemPropertiesWriter.write("\n");
                systemPropertiesWriter.write("# if you want to propagate a deployment on a cluster when a tomcat cluster is defined\n");
                systemPropertiesWriter.write("# tomee.cluster.deployment = false\n");

                systemPropertiesWriter.write("\n");
                systemPropertiesWriter.write("# openejb.system.apps = true\n");
                systemPropertiesWriter.write("# openejb.servicemanager.enabled = true\n");
                systemPropertiesWriter.write("# openejb.jmx.active = false\n");
                systemPropertiesWriter.write("# openejb.descriptors.output = false\n");
                systemPropertiesWriter.write("# openejb.strict.interface.declaration = false\n");
                systemPropertiesWriter.write("# openejb.conf.file = conf/tomee.xml\n");
                systemPropertiesWriter.write("# openejb.debuggable-vm-hackery = false\n");
                systemPropertiesWriter.write("# openejb.validation.skip = false\n");
                systemPropertiesWriter.write("# openejb.webservices.enabled = true\n");
                systemPropertiesWriter.write("# openejb.validation.output.level = MEDIUM\n");
                systemPropertiesWriter.write("# openejb.user.mbeans.list = *\n");
                systemPropertiesWriter.write("# openejb.deploymentId.format = {appId}/{ejbJarId}/{ejbName}\n");
                systemPropertiesWriter.write("# openejb.jndiname.format = {deploymentId}{interfaceType.annotationName}\n");
                systemPropertiesWriter.write("# openejb.deployments.package.include = .*\n");
                systemPropertiesWriter.write("# openejb.deployments.package.exclude = \n");
                systemPropertiesWriter.write("# openejb.autocreate.jta-datasource-from-non-jta-one = true\n");
                systemPropertiesWriter.write("# openejb.altdd.prefix = \n");
                systemPropertiesWriter.write("# org.apache.openejb.default.system.interceptors = \n");
                systemPropertiesWriter.write("# openejb.jndiname.failoncollision = true\n");
                systemPropertiesWriter.write("# openejb.wsAddress.format = /{ejbDeploymentId}\n");
                systemPropertiesWriter.write("# org.apache.openejb.server.webservices.saaj.provider = \n");
                systemPropertiesWriter.write("# openejb.nobanner = true\n");
                systemPropertiesWriter.write("# openejb.offline = false\n");
                systemPropertiesWriter.write("# openejb.jmx.active = true\n");
                systemPropertiesWriter.write("# openejb.exclude-include.order = include-exclude\n");
                systemPropertiesWriter.write("# openejb.additional.exclude =\n");
                systemPropertiesWriter.write("# openejb.additional.include =\n");
                systemPropertiesWriter.write("# openejb.crosscontext = false\n");
                systemPropertiesWriter.write("# openejb.jsessionid-support = \n");
                systemPropertiesWriter.write("# openejb.myfaces.disable-default-values = true\n");
                systemPropertiesWriter.write("# openejb.web.xml.major = \n");
                systemPropertiesWriter.write("# openjpa.Log = \n");
                systemPropertiesWriter.write("# openejb.jdbc.log = false\n");
                systemPropertiesWriter.write("# javax.persistence.provider = org.apache.openjpa.persistence.PersistenceProviderImpl\n");
                systemPropertiesWriter.write("# javax.persistence.transactionType = \n");
                systemPropertiesWriter.write("# javax.persistence.jtaDataSource = \n");
                systemPropertiesWriter.write("# javax.persistence.nonJtaDataSource = \n");

                systemPropertiesWriter.write("#\n");
                systemPropertiesWriter.write("# Properties for JAS RS\n");
                systemPropertiesWriter.write("# openejb.jaxrs.application = \n");
                systemPropertiesWriter.write("# openejb.cxf-rs.wadl-generator.ignoreRequests = false\n");
                systemPropertiesWriter.write("# openejb.cxf-rs.wadl-generator.ignoreMessageWriters = true\n");

                systemPropertiesWriter.write("#\n");
                systemPropertiesWriter.write("# These properties are only for cxf service (SOAP webservices) and TomEE+\n");
                systemPropertiesWriter.write("# If you don't use special tricks and sun default implementation, uncommenting these 4 lines forces TomEE to use it without overhead at all = \n");
                systemPropertiesWriter.write("# javax.xml.soap.MessageFactory = com.sun.xml.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl\n");
                systemPropertiesWriter.write("# javax.xml.soap.SOAPFactory = com.sun.xml.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl\n");
                systemPropertiesWriter.write("# javax.xml.soap.SOAPConnectionFactory = com.sun.xml.messaging.saaj.client.p2p.HttpSOAPConnectionFactory\n");
                systemPropertiesWriter.write("# javax.xml.soap.MetaFactory = com.sun.xml.messaging.saaj.soap.SAAJMetaFactoryImpl\n");
            } catch (final IOException e) {
                // ignored, this file is far to be mandatory
            } finally {
                if (systemPropertiesWriter != null) {
                    try {
                        systemPropertiesWriter.close();
                    } catch (final IOException e) {
                        // no-op
                    }
                }
            }
        }

        //
        // conf/web.xml
        //
        final JarFile openejbTomcatCommonJar;
        try {
            openejbTomcatCommonJar = new JarFile(paths.geOpenEJBTomcatCommonJar());
        } catch (final IOException e) {
            return;
        }
        final File webXmlFile = new File(confDir, "web.xml");
        final String webXml = Installers.readEntry(openejbTomcatCommonJar, "conf/web.xml", alerts);
        if (Installers.writeAll(webXmlFile, webXml, alerts)) {
            alerts.addInfo("Set jasper in production mode in TomEE web.xml");
        }
    }