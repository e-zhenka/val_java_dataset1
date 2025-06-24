private void createConnector(MBeanServer mbeanServer) throws MalformedObjectNameException, IOException {
        // Create the NamingService, needed by JSR 160
        try {
            if (registry == null) {
                LOG.debug("Creating RMIRegistry on port {}", connectorPort);
                registry = new JmxRegistry(connectorPort);
            }

            namingServiceObjectName = ObjectName.getInstance("naming:type=rmiregistry");

            // Do not use the createMBean as the mx4j jar may not be in the
            // same class loader than the server
            Class<?> cl = Class.forName("mx4j.tools.naming.NamingService");
            mbeanServer.registerMBean(cl.newInstance(), namingServiceObjectName);

            // set the naming port
            Attribute attr = new Attribute("Port", Integer.valueOf(connectorPort));
            mbeanServer.setAttribute(namingServiceObjectName, attr);
        } catch(ClassNotFoundException e) {
            LOG.debug("Probably not using JRE 1.4: {}", e.getLocalizedMessage());
        } catch (Throwable e) {
            LOG.debug("Failed to create local registry. This exception will be ignored.", e);
        }

        // Create the JMXConnectorServer
        String rmiServer = "";
        if (rmiServerPort != 0) {
            // This is handy to use if you have a firewall and need to force JMX to use fixed ports.
            rmiServer = ""+getConnectorHost()+":" + rmiServerPort;
        }

        final Map<String,Object> env = new HashMap<>();
        server = new RMIJRMPServerImpl(connectorPort, null, null, environment);

        final String serviceURL = "service:jmx:rmi://" + rmiServer + "/jndi/rmi://" +getConnectorHost()+":" + connectorPort + connectorPath;
        final JMXServiceURL url = new JMXServiceURL(serviceURL);

        connectorServer = new RMIConnectorServer(url, env, server, ManagementFactory.getPlatformMBeanServer());
        LOG.debug("Created JMXConnectorServer {}", connectorServer);
    }