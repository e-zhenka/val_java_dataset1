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
        
        sf = new JaxWsServerFactoryBean();
        sf.setServiceBean(new WrappedSoapActionGreeterImpl());
        sf.setAddress(add13);
        sf.setBus(bus);
        sf.create();
        
        sf = new JaxWsServerFactoryBean();
        sf.setServiceBean(new WrappedSoapActionGreeterImpl());
        sf.setAddress(add14);
        sf.setBus(bus);
        config.setVersion(Soap12.getInstance());
        sf.setBindingConfig(config);
        sf.create();
        
        sf = new JaxWsServerFactoryBean();
        sf.setServiceBean(new RPCLitSoapActionGreeterImpl());
        sf.setAddress(add15);
        sf.setBus(bus);
        sf.create();
        
        sf = new JaxWsServerFactoryBean();
        sf.setServiceBean(new RPCEncodedSoapActionGreeterImpl());
        sf.setAddress(add16);
        sf.setBus(bus);
        sf.create();
        
        sf = new JaxWsServerFactoryBean();
        sf.setServiceBean(new WrappedEncodedSoapActionGreeterImpl());
        sf.setAddress(add17);
        sf.setBus(bus);
        sf.create();
    }