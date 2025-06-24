public boolean matchesPassword(String password) {
        String token = getApiTokenInsecure();
        return MessageDigest.isEqual(password.getBytes(), token.getBytes());
    }