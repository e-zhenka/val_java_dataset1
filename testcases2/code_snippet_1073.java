private MockHttpServletRequestBuilder createChangePasswordRequest(ScimUser user, ExpiringCode code, boolean useCSRF, String password, String passwordConfirmation) throws Exception {
        MockHttpServletRequestBuilder post = post("/reset_password.do");
        if (useCSRF) {
            post.with(csrf());
        }
        post.param("code", code.getCode())
            .param("email", user.getPrimaryEmail())
            .param("password", password)
            .param("password_confirmation", passwordConfirmation);
        return post;
    }