@RequirePOST
        public FormValidation doCheckRepoUrl(@QueryParameter(fixEmpty = true) String value, @AncestorInPath AbstractProject project) throws IOException,
                ServletException {

            // Connect to URL and check content only if we have admin permission
            if (!Jenkins.get().hasPermission(Jenkins.ADMINISTER))
                return FormValidation.ok();

            if (value == null) // nothing entered yet
                value = "origin";

            if (!value.contains("/") && project != null) {
                GitSCM scm = (GitSCM) project.getScm();
                RemoteConfig remote = scm.getRepositoryByName(value);
                if (remote == null)
                    return FormValidation.errorWithMarkup("There is no remote with the name <code>" + value + "</code>");
                
                value = remote.getURIs().get(0).toString();
            }
            
            if (!value.endsWith("/"))
                value += '/';
            if (!URL_PATTERN.matcher(value).matches())
                return FormValidation.errorWithMarkup("The URL should end like <code>.../_git/foobar/</code>");

            final String finalValue = value;
            return new FormValidation.URLCheck() {
                @Override
                protected FormValidation check() throws IOException, ServletException {
                    try {
                        if (findText(open(new URL(finalValue)), REPOSITORY_BROWSER_LABEL)) {
                            return FormValidation.ok();
                        } else {
                            return FormValidation.error("This is a valid URL but it doesn't look like Microsoft TFS 2013");
                        }
                    } catch (IOException e) {
                        return handleIOException(finalValue, e);
                    }
                }
            }.check();
        }