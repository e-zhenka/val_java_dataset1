public @CheckForNull User getUser(String name) {
        return User.get(name, User.ALLOW_USER_CREATION_VIA_URL && hasPermission(ADMINISTER));
    }