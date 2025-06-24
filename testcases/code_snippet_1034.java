@Before
    public void setUp() throws Exception {
        if (originalDatabaseExternalMembers==null) {
            originalDefaultExternalMembers = (List<String>) getWebApplicationContext().getBean("defaultExternalMembers");
            originalDatabaseExternalMembers = getWebApplicationContext().getBean(JdbcScimGroupExternalMembershipManager.class).query("");
        }

        if(bootstrap == null){
            bootstrap = getWebApplicationContext().getBean(ScimExternalGroupBootstrap.class);
        }

        if(template == null) {
            template = getWebApplicationContext().getBean(JdbcTemplate.class);
        }

        template.update("delete from external_group_mapping");
        bootstrap.afterPropertiesSet();

        String adminToken = testClient.getClientCredentialsOAuthAccessToken("admin", "adminsecret",
                "clients.read clients.write clients.secret clients.admin");
        clientId = generator.generate().toLowerCase();
        clientSecret = generator.generate().toLowerCase();
        String authorities = "scim.read,scim.write,password.write,oauth.approvals,scim.create,other.scope";
        utils().createClient(this.getMockMvc(), adminToken, clientId, clientSecret, Collections.singleton("oauth"), Arrays.asList("foo","bar","scim.read"), Arrays.asList("client_credentials", "password"), authorities);
        scimReadToken = testClient.getClientCredentialsOAuthAccessToken(clientId, clientSecret,"scim.read password.write");
        scimWriteToken = testClient.getClientCredentialsOAuthAccessToken(clientId, clientSecret,"scim.write password.write");

        defaultExternalMembers = new LinkedList<>(originalDefaultExternalMembers);
        databaseExternalMembers = new LinkedList<>(originalDatabaseExternalMembers);

        scimUser = createUserAndAddToGroups(IdentityZone.getUaa(), new HashSet(Arrays.asList("scim.read", "scim.write", "scim.me")));
        scimReadUserToken = testClient.getUserOAuthAccessToken("cf","", scimUser.getUserName(), "password", "scim.read");
        identityClientToken = testClient.getClientCredentialsOAuthAccessToken("identity","identitysecret","");
    }