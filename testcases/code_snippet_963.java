private void createIdentityZoneHelper(String id) throws Exception {
        String identityClientWriteToken = testClient.getClientCredentialsOAuthAccessToken(
            "identity",
            "identitysecret",
            "zones.write");

        IdentityZone identityZone = new IdentityZone();
        SamlConfig samlConfig = new SamlConfig();
        samlConfig.setCertificate(SERVICE_PROVIDER_CERTIFICATE);
        samlConfig.setPrivateKey(SERVICE_PROVIDER_KEY);
        samlConfig.setPrivateKeyPassword(SERVICE_PROVIDER_KEY_PASSWORD);
        samlConfig.setEntityID(SERVICE_PROVIDER_ID);
        identityZone.getConfig().setSamlConfig(samlConfig);
        identityZone.getConfig().setIssuer(DEFAULT_ISSUER_URI);
        identityZone.setId(id);
        identityZone.setSubdomain(StringUtils.hasText(id) ? id : new RandomValueStringGenerator().generate());
        identityZone.setName("The Twiglet Zone");

        IdentityZoneConfiguration brandingConfig = setBranding(identityZone.getConfig());
        identityZone.setConfig(brandingConfig);


        getMockMvc().perform(
            post("/identity-zones")
                .header("Authorization", "Bearer " + identityClientWriteToken)
                .contentType(APPLICATION_JSON)
                .content(JsonUtils.writeValueAsString(identityZone)))
            .andExpect(status().is(HttpStatus.CREATED.value()));
    }