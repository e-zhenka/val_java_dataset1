private MockHttpServletRequestBuilder createChangePasswordRequest(ScimUser user, ExpiringCode code, boolean useCSRF, String password, String passwordConfirmation) throws Exception {
        return createChangePasswordRequest(user,code.getCode(),useCSRF, password,passwordConfirmation);
    }