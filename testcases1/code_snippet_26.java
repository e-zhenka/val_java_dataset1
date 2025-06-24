private User createAccount(StaplerRequest req, StaplerResponse rsp, boolean selfRegistration, String formView) throws ServletException, IOException {
        // form field validation
        // this pattern needs to be generalized and moved to stapler
        SignupInfo si = new SignupInfo(req);

        if(selfRegistration && !validateCaptcha(si.captcha))
            si.errorMessage = Messages.HudsonPrivateSecurityRealm_CreateAccount_TextNotMatchWordInImage();

        if(si.password1 != null && !si.password1.equals(si.password2))
            si.errorMessage = Messages.HudsonPrivateSecurityRealm_CreateAccount_PasswordNotMatch();

        if(!(si.password1 != null && si.password1.length() != 0))
            si.errorMessage = Messages.HudsonPrivateSecurityRealm_CreateAccount_PasswordRequired();

        if(si.username==null || si.username.length()==0)
            si.errorMessage = Messages.HudsonPrivateSecurityRealm_CreateAccount_UserNameRequired();
        else {
            User user = User.get(si.username, false);
            if (null != user)
                // Allow sign up. SCM people has no such property.
                if (user.getProperty(Details.class) != null)
                    si.errorMessage = Messages.HudsonPrivateSecurityRealm_CreateAccount_UserNameAlreadyTaken();
        }

        if(si.fullname==null || si.fullname.length()==0)
            si.fullname = si.username;

        if(si.email==null || !si.email.contains("@"))
            si.errorMessage = Messages.HudsonPrivateSecurityRealm_CreateAccount_InvalidEmailAddress();

        if (! User.isIdOrFullnameAllowed(si.username)) {
            si.errorMessage = hudson.model.Messages.User_IllegalUsername(si.username);
        }

        if (! User.isIdOrFullnameAllowed(si.fullname)) {
            si.errorMessage = hudson.model.Messages.User_IllegalFullname(si.fullname);
        }

        if(si.errorMessage!=null) {
            // failed. ask the user to try again.
            req.setAttribute("data",si);
            req.getView(this, formView).forward(req,rsp);
            return null;
        }

        // register the user
        User user = createAccount(si.username,si.password1);
        user.setFullName(si.fullname);
        try {
            // legacy hack. mail support has moved out to a separate plugin
            Class<?> up = Jenkins.getInstance().pluginManager.uberClassLoader.loadClass("hudson.tasks.Mailer$UserProperty");
            Constructor<?> c = up.getDeclaredConstructor(String.class);
            user.addProperty((UserProperty)c.newInstance(si.email));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to set the e-mail address",e);
        }
        user.save();
        return user;
    }