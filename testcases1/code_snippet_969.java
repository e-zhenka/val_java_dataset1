@GET
    @Produces({"application/json", "application/jwt" })
    public Response getUserInfo() {
        OAuthContext oauth = OAuthContextUtils.getContext(mc);
        UserInfo userInfo = null;
        if (userInfoProvider != null) {
            userInfo = userInfoProvider.getUserInfo(oauth.getClientId(), oauth.getSubject(),
                OAuthUtils.convertPermissionsToScopeList(oauth.getPermissions()));
        } else if (oauth.getSubject() instanceof OidcUserSubject) {
            OidcUserSubject oidcUserSubject = (OidcUserSubject)oauth.getSubject();
            userInfo = oidcUserSubject.getUserInfo();
            if (userInfo == null) {
                userInfo = createFromIdToken(oidcUserSubject.getIdToken());
            }
        }
        if (userInfo == null) {
            // Consider customizing the error code in case of UserInfo being not available
            return Response.serverError().build();
        }

        Object responseEntity = null;
        // UserInfo may be returned in a clear form as JSON
        if (super.isJwsRequired() || super.isJweRequired()) {
            Client client = null;
            if (oauthDataProvider != null) {
                client = oauthDataProvider.getClient(oauth.getClientId());
            }
            responseEntity = super.processJwt(new JwtToken(userInfo), client);
        } else {
            responseEntity = convertUserInfoToResponseEntity(userInfo);
        }
        return Response.ok(responseEntity).build();

    }