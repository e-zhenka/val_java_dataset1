public String getApiToken() {
        return Util.getDigestOf(apiToken.getPlainText());
    }