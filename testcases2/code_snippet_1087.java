@Override
    protected Details authenticate(String username, String password) throws AuthenticationException {
        Details u = loadUserByUsername(username);
        if (!u.isPasswordCorrect(password)) {
            String message;
            try {
                message = ResourceBundle.getBundle("org.acegisecurity.messages").getString("AbstractUserDetailsAuthenticationProvider.badCredentials");
            } catch (MissingResourceException x) {
                message = "Bad credentials";
            }
            throw new BadCredentialsException(message);
        }
        return u;
    }