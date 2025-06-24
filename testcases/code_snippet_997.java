@RequirePOST
        public FormValidation doCheckCredentialsId(StaplerRequest req, @AncestorInPath Item context, @QueryParameter String remoteBase, @QueryParameter String value) {
            // TODO suspiciously similar to SubversionSCM.ModuleLocation.DescriptorImpl.checkCredentialsId; refactor into shared method?
            // Test the connection only if we may use the credentials
            if (context == null && !Jenkins.get().hasPermission(Jenkins.ADMINISTER) ||
                context != null && !context.hasPermission(CredentialsProvider.USE_ITEM)) {
                return FormValidation.ok();
            }

            // if check remote is reporting an issue then we don't need to
            String url = Util.fixEmptyAndTrim(remoteBase);
            if (url == null)
                return FormValidation.ok();

            if(!URL_PATTERN.matcher(url).matches())
                return FormValidation.ok();

            try {
                String urlWithoutRevision = SvnHelper.getUrlWithoutRevision(url);

                SVNURL repoURL = SVNURL.parseURIDecoded(urlWithoutRevision);

                StandardCredentials credentials = value == null ? null :
                        CredentialsMatchers.firstOrNull(CredentialsProvider
                                .lookupCredentials(StandardCredentials.class, context, ACL.SYSTEM,
                                        URIRequirementBuilder.fromUri(repoURL.toString()).build()),
                                CredentialsMatchers.withId(value));
                if (checkRepositoryPath(repoURL, credentials)!=SVNNodeKind.NONE) {
                    // something exists; now check revision if any

                    SVNRevision revision = getRevisionFromRemoteUrl(url);
                    if (revision != null && !revision.isValid()) {
                        return FormValidation.errorWithMarkup(
                                hudson.scm.subversion.Messages.SubversionSCM_doCheckRemote_invalidRevision());
                    }

                    return FormValidation.ok();
                }

                SVNRepository repository = null;
                try {
                    repository = getRepository(repoURL, credentials,
                            Collections.emptyMap(), null);
                    long rev = repository.getLatestRevision();
                    // now go back the tree and find if there's anything that exists
                    String repoPath = getRelativePath(repoURL, repository);
                    String p = repoPath;
                    while(p.length()>0) {
                        p = SVNPathUtil.removeTail(p);
                        if(repository.checkPath(p,rev)==SVNNodeKind.DIR) {
                            // found a matching path
                            List<SVNDirEntry> entries = new ArrayList<>();
                            repository.getDir(p,rev,false,entries);

                            // build up the name list
                            List<String> paths = new ArrayList<>();
                            for (SVNDirEntry e : entries)
                                if(e.getKind()==SVNNodeKind.DIR)
                                    paths.add(e.getName());

                            String head = SVNPathUtil.head(repoPath.substring(p.length() + 1));
                            String candidate = EditDistance.findNearest(head, paths);

                            return FormValidation.error(
                                hudson.scm.subversion.Messages.SubversionSCM_doCheckRemote_badPathSuggest(p, head,
                                        candidate != null ? "/" + candidate : ""));
                        }
                    }

                    return FormValidation.error(
                        hudson.scm.subversion.Messages.SubversionSCM_doCheckRemote_badPath(repoPath));
                } finally {
                    if (repository != null)
                        repository.closeSession();
                }
            } catch (SVNException e) {
                LOGGER.log(Level.INFO, "Failed to access subversion repository "+url,e);
                String message = hudson.scm.subversion.Messages.SubversionSCM_doCheckRemote_exceptionMsg1(
                        Util.escape(url), Util.escape(e.getErrorMessage().getFullMessage()),
                        "javascript:document.getElementById('svnerror').style.display='block';"
                                + "document.getElementById('svnerrorlink').style.display='none';"
                                + "return false;")
                  + "<br/><pre id=\"svnerror\" style=\"display:none\">"
                  + Util.xmlEscape(Functions.printThrowable(e)) + "</pre>";
                return FormValidation.errorWithMarkup(message);
            }
        }