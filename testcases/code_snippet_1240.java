@BeforeClass
    public static void createServers() throws Exception {
        bus = BusFactory.getDefaultBus();
        JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setServiceBean(new SoapActionGreeterImpl());
        sf.setAddress(add11);
        sf.setBus(bus);
        sf.create();
        
        sf = new JaxWsServerFactoryBean();
        sf.setServiceBean(new SoapActionGreeterImpl());
        sf.setAddress(add12);
        sf.setBus(bus);
        SoapBindingConfiguration config = new SoapBindingConfiguration();
        config.setVersion(Soap12.getInstance());
        sf.setBindingConfig(config);
        sf.create();
    }