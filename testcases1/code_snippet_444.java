private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(NacosAuthConfig.AUTHORIZATION_HEADER);
        if (StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        }
        String jwt = request.getParameter(Constants.ACCESS_TOKEN);
        if (StringUtils.isNotBlank(jwt)) {
            return jwt;
        }
        return null;
    }