@Override
    protected void doExecute(CreateApiKeyRequest request, ActionListener<CreateApiKeyResponse> listener) {
        final Authentication authentication = securityContext.getAuthentication();
        if (authentication == null) {
            listener.onFailure(new IllegalStateException("authentication is required"));
        } else {
            rolesStore.getRoleDescriptors(new HashSet<>(Arrays.asList(authentication.getUser().roles())),
                ActionListener.wrap(roleDescriptors -> apiKeyService.createApiKey(authentication, request, roleDescriptors, listener),
                    listener::onFailure));
        }
    }