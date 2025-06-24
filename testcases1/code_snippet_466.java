@Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String secret = getSecret();
        Key key = new SecretKeySpec(Decoders.BASE64.decode(secret), getSignatureAlgorithm().getJcaName());
        
        Jws<Claims> jwt = Jwts.parser().
                setSigningKey(key).
                parseClaimsJws((String) token.getPrincipal());
        Map<String, Serializable> principal = getPrincipal(jwt);
        return new SimpleAuthenticationInfo(principal, ((String) token.getCredentials()).toCharArray(), getName());
    }