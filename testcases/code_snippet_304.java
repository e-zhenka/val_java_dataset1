private IdentityZone createZone(String id, HttpStatus expect, String token, IdentityZoneConfiguration zoneConfiguration) throws Exception {
        IdentityZone identityZone = getIdentityZone(id);
        identityZone.setConfig(zoneConfiguration);
        identityZone.getConfig().getSamlConfig().setPrivateKey(serviceProviderKey);
        identityZone.getConfig().getSamlConfig().setPrivateKeyPassword(serviceProviderKeyPassword);
        identityZone.getConfig().getSamlConfig().setCertificate(serviceProviderCertificate);
        Map<String, String> keys = new HashMap<>();
        keys.put("kid", "key");
        identityZone.getConfig().getTokenPolicy().setKeys(keys);
        identityZone.getConfig().getTokenPolicy().setActiveKeyId("kid");

        MvcResult result = getMockMvc().perform(
            post("/identity-zones")
                .header("Authorization", "Bearer " + token)
                .contentType(APPLICATION_JSON)
                .content(JsonUtils.writeValueAsString(identityZone)))
            .andExpect(status().is(expect.value()))
            .andReturn();

        if (expect.is2xxSuccessful()) {
            return JsonUtils.readValue(result.getResponse().getContentAsString(), IdentityZone.class);
        }
        return null;
    }