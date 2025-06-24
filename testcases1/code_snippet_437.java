public AuthenticationInfo loadAuthenticationInfo(JSONWebToken token) {
        Key key = getJWTKey();
        Jws<Claims> jwt;
        try {
            jwt = Jwts.parser().setSigningKey(key).parseClaimsJws(token.getPrincipal());
        } catch (JwtException e) {
            throw new AuthenticationException(e);
        }
        String credentials = legacyHashing ? token.getCredentials() : encryptPassword(token.getCredentials());
        Object principal = extractPrincipalFromWebToken(jwt);
        return new SimpleAuthenticationInfo(principal, credentials, getName());
    }