public void changeApiToken() throws IOException {
        user.checkPermission(Jenkins.ADMINISTER);
        _changeApiToken();
        if (user!=null)
            user.save();
    }