private ResetPasswordResponse changePasswordCodeAuthenticated(String code, String newPassword) {
        ExpiringCode expiringCode = expiringCodeStore.retrieveCode(code);
        if (expiringCode == null) {
            throw new InvalidCodeException("invalid_code", "Sorry, your reset password link is no longer valid. Please request a new one", 422);
        }
        String userId;
        String userName = null;
        Date passwordLastModified = null;
        String clientId = null;
        String redirectUri = null;
        try {
            PasswordChange change = JsonUtils.readValue(expiringCode.getData(), PasswordChange.class);
            userId = change.getUserId();
            userName = change.getUsername();
            passwordLastModified = change.getPasswordModifiedTime();
            clientId = change.getClientId();
            redirectUri = change.getRedirectUri();
        } catch (JsonUtils.JsonUtilException x) {
            userId = expiringCode.getData();
        }
        ScimUser user = scimUserProvisioning.retrieve(userId);
        try {
            if (isUserModified(user, expiringCode.getExpiresAt(), userName, passwordLastModified)) {
                throw new UaaException("Invalid password reset request.");
            }
            if (!user.isVerified()) {
                scimUserProvisioning.verifyUser(userId, -1);
            }
            if (scimUserProvisioning.checkPasswordMatches(userId, newPassword)) {
                throw new InvalidPasswordException("Your new password cannot be the same as the old password.", UNPROCESSABLE_ENTITY);
            }
            scimUserProvisioning.changePassword(userId, null, newPassword);
            publish(new PasswordChangeEvent("Password changed", getUaaUser(user), SecurityContextHolder.getContext().getAuthentication()));

            String redirectLocation = "home";
            if (!isEmpty(clientId) && !isEmpty(redirectUri)) {
                try {
                    ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
                    Set<String> redirectUris = clientDetails.getRegisteredRedirectUri() == null ? Collections.emptySet() :
                        clientDetails.getRegisteredRedirectUri();
                    String matchingRedirectUri = UaaUrlUtils.findMatchingRedirectUri(redirectUris, redirectUri, null);
                    if (matchingRedirectUri != null) {
                        redirectLocation = matchingRedirectUri;
                    }
                } catch (NoSuchClientException nsce) {}
            }
            return new ResetPasswordResponse(user, redirectLocation, clientId);
        } catch (Exception e) {
            publish(new PasswordChangeFailureEvent(e.getMessage(), getUaaUser(user), SecurityContextHolder.getContext().getAuthentication()));
            throw e;
        }
    }