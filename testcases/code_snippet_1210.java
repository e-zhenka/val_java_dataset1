@Override
    protected void doExecute(CreateApiKeyRequest request, ActionListener<CreateApiKeyResponse> listener) {
        final Authentication authentication = securityContext.getAuthentication();
        if (authentication == null) {
            listener.onFailure(new IllegalStateException("authentication is required"));
        } else {
            if (Authentication.AuthenticationType.API_KEY == authentication.getAuthenticationType() && grantsAnyPrivileges(request)) {
                listener.onFailure(new IllegalArgumentException(
                    "creating derived api keys requires an explicit role descriptor that is empty (has no privileges)"));
                return;
            }
            rolesStore.getRoleDescriptors(new HashSet<>(Arrays.asList(authentication.getUser().roles())),
                ActionListener.wrap(roleDescriptors -> apiKeyService.createApiKey(authentication, request, roleDescriptors, listener),
                    listener::onFailure));
        }
    }