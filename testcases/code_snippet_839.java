@Restricted(NoExternalUse.class)
    public User getUser(String id) {
        return User.getById(id, User.ALLOW_USER_CREATION_VIA_URL && hasPermission(Jenkins.ADMINISTER));
    }