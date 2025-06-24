@Before
    public void createCaptor() throws Exception {
        applicationEventPublisher = mock(ApplicationEventPublisher.class);
        ClientAdminEventPublisher eventPublisher = (ClientAdminEventPublisher) getWebApplicationContext().getBean("clientAdminEventPublisher");
        originalApplicationEventPublisher = eventPublisher.getPublisher();
        eventPublisher.setApplicationEventPublisher(applicationEventPublisher);
        captor = ArgumentCaptor.forClass(AbstractUaaEvent.class);
        scimUserEndpoints = getWebApplicationContext().getBean(ScimUserEndpoints.class);
        scimGroupEndpoints = getWebApplicationContext().getBean(ScimGroupEndpoints.class);

        testClient = new TestClient(getMockMvc());
        testAccounts = UaaTestAccounts.standard(null);
        adminToken = testClient.getClientCredentialsOAuthAccessToken(
                testAccounts.getAdminClientId(),
                testAccounts.getAdminClientSecret(),
                "clients.admin clients.read clients.write clients.secret scim.read scim.write");

        testPassword = "password";
        String username = new RandomValueStringGenerator().generate() + "@test.org";
        testUser = new ScimUser(null, username, "givenname","familyname");
        testUser.setPrimaryEmail(username);
        testUser.setPassword(testPassword);
        testUser = MockMvcUtils.utils().createUser(getMockMvc(), adminToken, testUser);
        testUser.setPassword(testPassword);

        applicationEventPublisher = mock(ApplicationEventPublisher.class);
        eventPublisher.setApplicationEventPublisher(applicationEventPublisher);
        captor = ArgumentCaptor.forClass(AbstractUaaEvent.class);
    }