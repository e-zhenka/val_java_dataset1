@After
    public void cleanUpDomainList() throws Exception {
        IdentityZoneHolder.clear();
        IdentityProvider<UaaIdentityProviderDefinition> uaaProvider = getWebApplicationContext().getBean(JdbcIdentityProviderProvisioning.class).retrieveByOrigin(UAA, IdentityZone.getUaa().getId());
        uaaProvider.getConfig().setEmailDomain(null);
        getWebApplicationContext().getBean(JdbcIdentityProviderProvisioning.class).update(uaaProvider);
    }