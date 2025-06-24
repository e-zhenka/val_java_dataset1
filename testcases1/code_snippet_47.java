@Override
    public void setupRoutes() {
        path(controllerBasePath(), () -> {
            before("", mimeType, this::setContentType);
            before("/*", mimeType, this::setContentType);
            before("", mimeType, this::verifyContentType);
            before("/*", mimeType, this::verifyContentType);

            // change the line below to enable appropriate security
            before("", mimeType, this.apiAuthenticationHelper::checkAdminUserAnd403);

            get("", mimeType, this::show);

            post("", mimeType, this::createOrUpdate);
            put("", mimeType, this::createOrUpdate);

            delete("", mimeType, this::deleteBackupConfig);
        });
    }