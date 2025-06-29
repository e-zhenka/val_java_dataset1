@SuppressWarnings("resource")
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

        // configure the RMI registry to use the socket factories we just created
        Registry registry = LocateRegistry.createRegistry(port,
                                                          (RMIClientSocketFactory) env.get(RMIConnectorServer.RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE),
                                                          (RMIServerSocketFactory) env.get(RMIConnectorServer.RMI_SERVER_SOCKET_FACTORY_ATTRIBUTE));

        // Configure authn, using a JMXAuthenticator which either wraps a set log LoginModules configured
        // via a JAAS configuration entry, or one which delegates to the standard file based authenticator.
        // Authn is disabled if com.sun.management.jmxremote.authenticate=false
        env.putAll(configureJmxAuthentication());

        // Configure authz - if a custom proxy class is specified an instance will be returned.
        // If not, but a location for the standard access file is set in system properties, the
        // return value is null, and an entry is added to the env map detailing that location
        // If neither method is specified, no access control is applied
        MBeanServerForwarder authzProxy = configureJmxAuthorization(env);

        // Mark the JMX server as a permanently exported object. This allows the JVM to exit with the
        // server running and also exempts it from the distributed GC scheduler which otherwise would
        // potentially attempt a full GC every `sun.rmi.dgc.server.gcInterval` millis (default is 3600000ms)
        // For more background see:
        //   - CASSANDRA-2967
        //   - https://www.jclarity.com/2015/01/27/rmi-system-gc-unplugged/
        //   - https://bugs.openjdk.java.net/browse/JDK-6760712
        env.put("jmx.remote.x.daemon", "true");

        // Set the port used to create subsequent connections to exported objects over RMI. This simplifies
        // configuration in firewalled environments, but it can't be used in conjuction with SSL sockets.
        // See: CASSANDRA-7087
        int rmiPort = Integer.getInteger("com.sun.management.jmxremote.rmi.port", 0);

        // We create the underlying RMIJRMPServerImpl so that we can manually bind it to the registry,
        // rather then specifying a binding address in the JMXServiceURL and letting it be done automatically
        // when the server is started. The reason for this is that if the registry is configured with SSL
        // sockets, the JMXConnectorServer acts as its client during the binding which means it needs to
        // have a truststore configured which contains the registry's certificate. Manually binding removes
        // this problem.
        // See CASSANDRA-12109.
        RMIJRMPServerImpl server = new RMIJRMPServerImpl(rmiPort,
                                                         (RMIClientSocketFactory) env.get(RMIConnectorServer.RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE),
                                                         (RMIServerSocketFactory) env.get(RMIConnectorServer.RMI_SERVER_SOCKET_FACTORY_ATTRIBUTE),
                                                         env);
        JMXServiceURL serviceURL = new JMXServiceURL("rmi", null, rmiPort);
        RMIConnectorServer jmxServer = new RMIConnectorServer(serviceURL, env, server, ManagementFactory.getPlatformMBeanServer());

        // If a custom authz proxy was created, attach it to the server now.
        if (authzProxy != null)
            jmxServer.setMBeanServerForwarder(authzProxy);
        jmxServer.start();

        registry.rebind("jmxrmi", server);
        logJmxServiceUrl(serverAddress, port);
        return jmxServer;
    }