@RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView recover(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        String usernameOrEmail = StringUtils.trimToNull(request.getParameter("usernameOrEmail"));

        if (usernameOrEmail != null) {

            map.put("usernameOrEmail", usernameOrEmail);
            User user = getUserByUsernameOrEmail(usernameOrEmail);

            boolean captchaOk;
            if (settingsService.isCaptchaEnabled()) {
                String recaptchaResponse = request.getParameter("g-recaptcha-response");
                ReCaptcha captcha = new ReCaptcha(settingsService.getRecaptchaSecretKey());
                captchaOk = recaptchaResponse != null && captcha.isValid(recaptchaResponse);
            } else {
                captchaOk = true;
            }
            
            if (!captchaOk) {
                map.put("error", "recover.error.invalidcaptcha");
            } else if (user == null) {
                map.put("error", "recover.error.usernotfound");
            } else if (user.getEmail() == null) {
                map.put("error", "recover.error.noemail");
            } else {
                String password = RandomStringUtils.randomAlphanumeric(8);
                if (emailPassword(password, user.getUsername(), user.getEmail())) {
                    map.put("sentTo", user.getEmail());
                    user.setLdapAuthenticated(false);
                    user.setPassword(password);
                    securityService.updateUser(user);
                } else {
                    map.put("error", "recover.error.sendfailed");
                }
            }
        }

        if (settingsService.isCaptchaEnabled()) {
            map.put("recaptchaSiteKey", settingsService.getRecaptchaSiteKey());
        }

        return new ModelAndView("recover", "model", map);
    }