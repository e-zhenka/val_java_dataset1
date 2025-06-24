@SuppressWarnings("unchecked")
    private Jwt<?, Claims> authenticateToken(final String token) throws AuthenticationException {
        try {
            Jwt<?, Claims> jwt = Jwts.parserBuilder().setSigningKey(validationKey).build().parseClaimsJws(token);

            if (audienceClaim != null) {
                Object object = jwt.getBody().get(audienceClaim);
                if (object == null) {
                    throw new JwtException("Found null Audience in token, for claimed field: " + audienceClaim);
                }

                if (object instanceof List) {
                    List<String> audiences = (List<String>) object;
                    // audience not contains this broker, throw exception.
                    if (!audiences.stream().anyMatch(audienceInToken -> audienceInToken.equals(audience))) {
                        throw new AuthenticationException("Audiences in token: [" + String.join(", ", audiences)
                                                          + "] not contains this broker: " + audience);
                    }
                } else if (object instanceof String) {
                    if (!object.equals(audience)) {
                        throw new AuthenticationException("Audiences in token: [" + object
                                                          + "] not contains this broker: " + audience);
                    }
                } else {
                    // should not reach here.
                    throw new AuthenticationException("Audiences in token is not in expected format: " + object);
                }
            }

            return jwt;
        } catch (JwtException e) {
            throw new AuthenticationException("Failed to authentication token: " + e.getMessage());
        }
    }