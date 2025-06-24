public String getApiToken() {
        String p = apiToken.getPlainText();
        if (p.equals(Util.getDigestOf(Jenkins.getInstance().getSecretKey()+":"+user.getId()))) {
            // if the current token is the initial value created by pre SECURITY-49 Jenkins, we can't use that.
            // force using the newer value
            apiToken = Secret.fromString(p=API_KEY_SEED.mac(user.getId()));
        }
        return Util.getDigestOf(p);
    }