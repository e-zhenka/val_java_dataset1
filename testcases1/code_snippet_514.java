@RequestMapping(value = "/accept.do", method = POST)
    public String acceptInvitation(@RequestParam("password") String password,
                                   @RequestParam("password_confirmation") String passwordConfirmation,
                                   @RequestParam("code") String code,
                                   Model model,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws IOException {

        PasswordConfirmationValidation validation = new PasswordConfirmationValidation(password, passwordConfirmation);

        UaaPrincipal principal =  (UaaPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        final ExpiringCode expiringCode = expiringCodeStore.retrieveCode(code);

        if (expiringCode == null || expiringCode.getData() == null) {
            logger.debug("Failing invitation. Code not found.");
            SecurityContextHolder.clearContext();
            return handleUnprocessableEntity(model, response, "error_message_code", "code_expired", "invitations/accept_invite");
        }
        Map<String,String> data = JsonUtils.readValue(expiringCode.getData(), new TypeReference<Map<String,String>>() {});
        if (principal == null || data.get("user_id") == null || !data.get("user_id").equals(principal.getId())) {
            logger.debug("Failing invitation. Code and user ID mismatch.");
            SecurityContextHolder.clearContext();
            return handleUnprocessableEntity(model, response, "error_message_code", "code_expired", "invitations/accept_invite");
        }

        final String newCode = expiringCodeStore.generateCode(expiringCode.getData(), new Timestamp(System.currentTimeMillis() + (10 * 60 * 1000)), expiringCode.getIntent()).getCode();
        if (!validation.valid()) {
           return processErrorReload(newCode, model, principal.getEmail(), response, "error_message_code", validation.getMessageCode());
//           return handleUnprocessableEntity(model, response, "error_message_code", validation.getMessageCode(), "invitations/accept_invite");
        }
        try {
            passwordValidator.validate(password);
        } catch (InvalidPasswordException e) {
            return processErrorReload(newCode, model, principal.getEmail(), response, "error_message", e.getMessagesAsOneString());
//            return handleUnprocessableEntity(model, response, "error_message", e.getMessagesAsOneString(), "invitations/accept_invite");
        }
        AcceptedInvitation invitation;
        try {
            invitation = invitationsService.acceptInvitation(newCode, password);
        } catch (HttpClientErrorException e) {
            return handleUnprocessableEntity(model, response, "error_message_code", "code_expired", "invitations/accept_invite");
        }
        principal = new UaaPrincipal(
            invitation.getUser().getId(),
            invitation.getUser().getUserName(),
            invitation.getUser().getPrimaryEmail(),
            invitation.getUser().getOrigin(),
            invitation.getUser().getExternalId(),
            IdentityZoneHolder.get().getId()
        );
        UaaAuthentication authentication = new UaaAuthentication(principal, UaaAuthority.USER_AUTHORITIES, new UaaAuthenticationDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return "redirect:" + invitation.getRedirectUri();
    }