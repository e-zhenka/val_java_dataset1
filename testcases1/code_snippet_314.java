public @CheckForNull User getUser(String name) {
        return User.get(name,hasPermission(ADMINISTER));
    }