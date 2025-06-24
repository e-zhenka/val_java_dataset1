@Override
    protected Details authenticate(String username, String password) throws AuthenticationException {
        Details u = loadUserByUsername(username);
        if (!u.isPasswordCorrect(password))
            throw new BadCredentialsException("Failed to login as "+username);
        return u;
    }