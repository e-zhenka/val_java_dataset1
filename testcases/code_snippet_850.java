public static JMXConnectorServer createJMXServer(int port, boolean local)
    throws IOException
    {
        Map<String, Object> env = new HashMap<>();

        InetAddress serverAddress = null;
        if (local)
        {
            serverAddress = InetAddress.getLoopbackAddress();
            System.setProperty("java.rmi.server.hostname", serverAddress.getHostAddress());
        }

        // Configure the RMI client & server socket factories, including SSL config.
        env.putAll(configureJmxSocketFactories(serverAddress, local));


        // Configure authn, using a JMXAuthenticator which either wraps a set log LoginModules configured
        // via a JAAS configuration entry, or one which delegates to the standard file based authenticator.
        // Authn is disabled if com.sun.management.jmxremote.authenticate=false
        env.putAll(configureJmxAuthentication());

        // Configure authz - if a custom proxy class is specified an instance will be returned.
        // If not, but a location for the standard access file is set in system properties, the
        // return value is null, and an entry is added to the env map detailing that location
        // If neither method is specified, no access control is applied
        MBeanServerForwarder authzProxy = configureJmxAuthorization(env);

        // Make sure we use our custom exporter so a full GC doesn't get scheduled every
        // sun.rmi.dgc.server.gcInterval millis (default is 3600000ms/1 hour)
        env.put(RMIExporter.EXPORTER_ATTRIBUTE, new Exporter());


        int rmiPort = Integer.getInteger("com.sun.management.jmxremote.rmi.port", 0);
        JMXConnectorServer jmxServer =
            JMXConnectorServerFactory.newJMXConnectorServer(new JMXServiceURL("rmi", null, rmiPort),
                                                            env,
                                                            ManagementFactory.getPlatformMBeanServer());

        // If a custom authz proxy was created, attach it to the server now.
        if (authzProxy != null)
            jmxServer.setMBeanServerForwarder(authzProxy);

        jmxServer.start();

        // use a custom Registry to avoid having to interact with it internally using the remoting interface
        configureRMIRegistry(port, env);

        logJmxServiceUrl(serverAddress, port);
        return jmxServer;
    }