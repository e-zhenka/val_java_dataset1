public void changeApiToken() throws IOException {
        _changeApiToken();
        if (user!=null)
            user.save();
    }