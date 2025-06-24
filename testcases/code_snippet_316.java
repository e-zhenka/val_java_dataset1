@Override
    public BuildWrapper createBuildWrapper(AbstractBuild<?,?> build) {
        return new BuildWrapper() {
            @Override
            public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
            	if (!StringUtils.isEmpty(location) && !StringUtils.isEmpty(file.getName())) {
            	    listener.getLogger().println("Copying file to "+location);
                    FilePath ws = build.getWorkspace();
                    if (ws == null) {
                        throw new IllegalStateException("The workspace should be created when setUp method is called");
                    }
                    if (!ALLOW_FOLDER_TRAVERSAL_OUTSIDE_WORKSPACE && (PROHIBITED_DOUBLE_DOT.matcher(location).matches() || !ws.isDescendant(location))) {
                        listener.error("Rejecting file path escaping base directory with relative path: " + location);
                        // force the build to fail
                        return null;
                    }
                    FilePath locationFilePath = ws.child(location);
                    locationFilePath.getParent().mkdirs();
            	    locationFilePath.copyFrom(file);
                    locationFilePath.copyTo(new FilePath(getLocationUnderBuild(build)));
            	}
                return new Environment() {};
            }
        };
    }