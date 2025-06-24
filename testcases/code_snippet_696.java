private static void maybeInitJmx()
    {
        String jmxPort = System.getProperty("com.sun.management.jmxremote.port");

        if (jmxPort == null)
        {
            logger.warn("JMX is not enabled to receive remote connections. Please see cassandra-env.sh for more info.");

            jmxPort = System.getProperty("cassandra.jmx.local.port");

            if (jmxPort == null)
            {
                logger.error("cassandra.jmx.local.port missing from cassandra-env.sh, unable to start local JMX service." + jmxPort);
            }
            else
            {
                System.setProperty("java.rmi.server.hostname", InetAddress.getLoopbackAddress().getHostAddress());

                try
                {
                    RMIServerSocketFactory serverFactory = new RMIServerSocketFactoryImpl();
                    Map<String, Object> env = new HashMap<>();
                    env.put(RMIConnectorServer.RMI_SERVER_SOCKET_FACTORY_ATTRIBUTE, serverFactory);
                    env.put("jmx.remote.rmi.server.credential.types",
                        new String[] { String[].class.getName(), String.class.getName() });
                    Registry registry = new JmxRegistry(Integer.valueOf(jmxPort), null, serverFactory, "jmxrmi");
                    JMXServiceURL url = new JMXServiceURL(String.format("service:jmx:rmi://localhost/jndi/rmi://localhost:%s/jmxrmi", jmxPort));
                    @SuppressWarnings("resource")
                    RMIJRMPServerImpl server = new RMIJRMPServerImpl(Integer.valueOf(jmxPort),
                                                                     null,
                                                                     (RMIServerSocketFactory) env.get(RMIConnectorServer.RMI_SERVER_SOCKET_FACTORY_ATTRIBUTE),
                                                                     env);
                    jmxServer = new RMIConnectorServer(url, env, server, ManagementFactory.getPlatformMBeanServer());
                    jmxServer.start();
                    ((JmxRegistry)registry).setRemoteServerStub(server.toStub());
                }
                catch (IOException e)
                {
                    logger.error("Error starting local jmx server: ", e);
                }
            }
        }
        else
        {
            logger.info("JMX is enabled to receive remote connections on port: " + jmxPort);
        }
    }