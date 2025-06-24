private JMXConnectorServer createServer(String serverName,
            String bindAddress, int theRmiRegistryPort, int theRmiServerPort,
            Map<String,Object> theEnv,
            RMIClientSocketFactory registryCsf, RMIServerSocketFactory registrySsf,
            RMIClientSocketFactory serverCsf, RMIServerSocketFactory serverSsf) {

        if (bindAddress == null) {
            bindAddress = "localhost";
        }

        String url = "service:jmx:rmi://" + bindAddress;
        JMXServiceURL serviceUrl;
        try {
            serviceUrl = new JMXServiceURL(url);
        } catch (MalformedURLException e) {
            log.error(sm.getString("jmxRemoteLifecycleListener.invalidURL", serverName, url), e);
            return null;
        }

        RMIConnectorServer cs = null;
        try {
            RMIJRMPServerImpl server = new RMIJRMPServerImpl(
                    rmiServerPortPlatform, serverCsf, serverSsf, theEnv);
            cs = new RMIConnectorServer(serviceUrl, theEnv, server,
                    ManagementFactory.getPlatformMBeanServer());
            cs.start();
            Remote jmxServer = server.toStub();
            // Create the RMI registry
            try {
                new JmxRegistry(theRmiRegistryPort, registryCsf, registrySsf, "jmxrmi", jmxServer);
            } catch (RemoteException e) {
                log.error(sm.getString(
                        "jmxRemoteLifecycleListener.createRegistryFailed",
                        serverName, Integer.toString(theRmiRegistryPort)), e);
                return null;
            }
            log.info(sm.getString("jmxRemoteLifecycleListener.start",
                    Integer.toString(theRmiRegistryPort),
                    Integer.toString(theRmiServerPort), serverName));
        } catch (IOException e) {
            log.error(sm.getString(
                    "jmxRemoteLifecycleListener.createServerFailed",
                    serverName), e);
        }
        return cs;
    }