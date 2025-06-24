public HttpResponse doPostCredential(@QueryParameter String username, @QueryParameter String password) throws IOException, ServletException {
            this.username = username;
            this.password = Secret.fromString(password);
            save();
            return HttpResponses.redirectTo("credentialOK");
        }