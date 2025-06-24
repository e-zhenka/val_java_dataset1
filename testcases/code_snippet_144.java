@RequestMapping(value = "/oauth/authorize", method = RequestMethod.POST, params = OAuth2Utils.USER_OAUTH_APPROVAL)
    public View approveOrDeny(@RequestParam Map<String, String> approvalParameters, Map<String, ?> model,
                              SessionStatus sessionStatus, Principal principal) {

        if (!(principal instanceof Authentication)) {
            sessionStatus.setComplete();
            throw new InsufficientAuthenticationException(
              "User must be authenticated with Spring Security before authorizing an access token.");
        }

        AuthorizationRequest authorizationRequest = (AuthorizationRequest) model.get("authorizationRequest");

        if (authorizationRequest == null) {
            sessionStatus.setComplete();
            throw new InvalidRequestException("Cannot approve uninitialized authorization request.");
        }

        // Check to ensure the Authorization Request was not modified during the user approval step
        @SuppressWarnings("unchecked")
        Map<String, Object> originalAuthorizationRequest = (Map<String, Object>) model.get("org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint.ORIGINAL_AUTHORIZATION_REQUEST");
        if (isAuthorizationRequestModified(authorizationRequest, originalAuthorizationRequest)) {
            throw new InvalidRequestException("Changes were detected from the original authorization request.");
        }

        try {
            Set<String> responseTypes = authorizationRequest.getResponseTypes();
            String grantType = deriveGrantTypeFromResponseType(responseTypes);

            authorizationRequest.setApprovalParameters(approvalParameters);
            authorizationRequest = userApprovalHandler.updateAfterApproval(authorizationRequest,
              (Authentication) principal);
            boolean approved = userApprovalHandler.isApproved(authorizationRequest, (Authentication) principal);
            authorizationRequest.setApproved(approved);

            if (authorizationRequest.getRedirectUri() == null) {
                sessionStatus.setComplete();
                throw new InvalidRequestException("Cannot approve request when no redirect URI is provided.");
            }

            if (!authorizationRequest.isApproved()) {
                return new RedirectView(getUnsuccessfulRedirect(authorizationRequest,
                  new UserDeniedAuthorizationException("User denied access"), responseTypes.contains("token")),
                  false, true, false);
            }

            if (responseTypes.contains("token") || responseTypes.contains("id_token")) {
                return getImplicitGrantOrHybridResponse(
                  authorizationRequest,
                  (Authentication) principal,
                  grantType
                ).getView();
            }

            return getAuthorizationCodeResponse(authorizationRequest, (Authentication) principal);
        } finally {
            sessionStatus.setComplete();
        }

    }