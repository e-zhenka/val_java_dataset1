public String getRepoPassword() {
        if (repoPassword != null) {
            String plainText = repoPassword.getPlainText();
            if (!plainText.isEmpty()) {
                return plainText;
            }
        }
        return "admin";
    }