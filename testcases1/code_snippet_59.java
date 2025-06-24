public boolean matchesPassword(String password) {
        return  getApiTokenInsecure().equals(password);
    }