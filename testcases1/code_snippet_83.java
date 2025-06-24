@RequestMapping(value = "/accept.do", method = POST)
    public String acceptInvitation(@RequestParam("password") String password,
                                   @RequestParam("password_confirmation") String passwordConfirmation,
                                   @RequestParam("code") String code,
                                   Model model,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws IOException {

        PasswordConfirmationValidation validation = new PasswordConfirmationValidation(password, passwordConfirmation);

        UaaPrincipal principal =  (UaaPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!validation.valid()) {
           return processErrorReload(code, model, principal.getEmail(), response, "error_message_code", validation.getMessageCode());
//           return handleUnprocessableEntity(model, response, "error_message_code", validation.getMessageCode(), "invitations/accept_invite");
        }
        try {
            passwordValidator.validate(password);
        } catch (InvalidPasswordException e) {
            return processErrorReload(code, model, principal.getEmail(), response, "error_message", e.getMessagesAsOneString());
//            return handleUnprocessableEntity(model, response, "error_message", e.getMessagesAsOneString(), "invitations/accept_invite");
        }
        AcceptedInvitation invitation;
        try {
            invitation = invitationsService.acceptInvitation(code, password);
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