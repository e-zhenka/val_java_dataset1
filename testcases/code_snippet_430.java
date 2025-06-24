@Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String secret = getSecret();
        Key key = new SecretKeySpec(Decoders.BASE64.decode(secret), getSignatureAlgorithm().getJcaName());
        
        Jwt jwt = Jwts.parser().
                setSigningKey(key).
                parse((String) token.getPrincipal());
        Map<String, Serializable> principal = getPrincipal(jwt);
        return new SimpleAuthenticationInfo(principal, ((String) token.getCredentials()).toCharArray(), getName());
    }