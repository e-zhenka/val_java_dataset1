@Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        Connector c = getTomcatInstance().getConnector();
        c.setProperty("secretRequired", "false");
        c.setProperty("allowedRequestAttributesPattern", "MYATTRIBUTE.*");
    }