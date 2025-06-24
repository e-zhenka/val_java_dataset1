@RequirePOST
        public HttpResponse doPostCredential(@QueryParameter String username, @QueryParameter String password) throws IOException, ServletException {
            Jenkins.getInstance().checkPermission(Jenkins.ADMINISTER);
            this.username = username;
            this.password = Secret.fromString(password);
            save();
            return HttpResponses.redirectTo("credentialOK");
        }