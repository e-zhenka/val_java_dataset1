@Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        if (cloud != null) return;
        try {
            cloud = new CloudFactory().getCloud();
        } catch (CloudException e) {
            return; // not running on a known cloud environment, so nothing to do
        }

        for (ServiceInfo serviceInfo : cloud.getServiceInfos()) {
            if (serviceInfo instanceof SsoServiceInfo) {
                Map<String, Object> map = new HashMap<>();
                SsoServiceInfo ssoServiceInfo = (SsoServiceInfo) serviceInfo;
                map.put("security.oauth2.client.clientId", ssoServiceInfo.getClientId());
                map.put("security.oauth2.client.clientSecret", ssoServiceInfo.getClientSecret());
                map.put("security.oauth2.client.accessTokenUri", ssoServiceInfo.getAuthDomain() + "/oauth/token");
                map.put("security.oauth2.client.userAuthorizationUri", ssoServiceInfo.getAuthDomain() + "/oauth/authorize");
                map.put("ssoServiceUrl", ssoServiceInfo.getAuthDomain());
                map.put("security.oauth2.resource.userInfoUri", ssoServiceInfo.getAuthDomain() + "/userinfo");
                map.put("security.oauth2.resource.tokenInfoUri", ssoServiceInfo.getAuthDomain() + "/check_token");
                map.put("security.oauth2.resource.jwk.key-set-uri", ssoServiceInfo.getAuthDomain() + "/token_keys");
                map.put("sso.connector.cloud.available", "success");
                MapPropertySource mapPropertySource = new MapPropertySource("vcapPivotalSso", map);

                event.getEnvironment().getPropertySources().addFirst(mapPropertySource);
            }
        }
    }