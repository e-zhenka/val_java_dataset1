private IdentityZone createZone(String id, HttpStatus expect, String token, IdentityZoneConfiguration zoneConfiguration) throws Exception {
        Map<String, String> keys = new HashMap<>();
        keys.put("kid", "key");
        zoneConfiguration.getTokenPolicy().setKeys(keys);
        zoneConfiguration.getTokenPolicy().setActiveKeyId("kid");
        zoneConfiguration.getTokenPolicy().setKeys(keys);

        return createZone(id, expect, "" , token, zoneConfiguration);
    }