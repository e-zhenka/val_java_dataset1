@Override
    protected boolean op(String name, File path) throws SecurityException {
        if (SystemProperties.getBoolean(SKIP_PROPERTY)) {
            LOGGER.log(Level.FINE, () -> "Skipping check for '" + name + "' on '" + path + "'");
            return false;
        }
        if (!(context instanceof Computer)) {
            LOGGER.log(Level.FINE, "No context provided for path access: " + path);
            return false;
        }
        Computer c = (Computer) context;

        final Jenkins jenkins = Jenkins.get();

        String patternString;
        try {
            patternString = Jenkins.expandVariablesForDirectory(jenkins.getRawBuildsDir(), "(.+)", "\\Q" + Jenkins.get().getRootDir().getCanonicalPath().replace('\\', '/') + "\\E/jobs/(.+)") + "/[0-9]+(/.*)?";
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to obtain canonical path to Jenkins home directory", e);
            throw new SecurityException("Failed to obtain canonical path"); // Minimal details
        }
        final Pattern pattern = Pattern.compile(patternString);

        String absolutePath;
        try {
            absolutePath = path.getCanonicalPath().replace('\\', '/');
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to obtain canonical path to '" + path + "'", e);
            throw new SecurityException("Failed to obtain canonical path"); // Minimal details
        }
        if (!pattern.matcher(absolutePath).matches()) {
            /* This is not a build directory, so another filter will take care of it */
            LOGGER.log(Level.FINE, "Not a build directory, so skipping: " + absolutePath);
            return false;
        }

        final Path thePath = path.getAbsoluteFile().toPath();
        for (Executor executor : c.getExecutors()) {
            Run<?, ?> build = findRun(executor.getCurrentExecutable());
            if (build == null) {
                continue;
            }
            final Path buildDir = build.getRootDir().getAbsoluteFile().toPath();
            // If the directory being accessed is for a build currently running on this node, allow it
            if (thePath.startsWith(buildDir)) {
                return false;
            }
        }

        final String computerName = c.getName();
        if (SystemProperties.getBoolean(FAIL_PROPERTY, true)) {
            // This filter can only prohibit by throwing a SecurityException; it never allows on its own.
            LOGGER.log(Level.WARNING, "Rejecting unexpected agent-to-controller file path access: Agent '" + computerName + "' is attempting to access '" + absolutePath + "' using operation '" + name + "'. Learn more: https://www.jenkins.io/redirect/security-144/");
            throw new SecurityException("Agent tried to access build directory of a build not currently running on this system. Learn more: https://www.jenkins.io/redirect/security-144/");
        } else {
            LOGGER.log(Level.WARNING, "Unexpected agent-to-controller file path access: Agent '" + computerName + "' is accessing '" + absolutePath + "' using operation '" + name + "'. Learn more: https://www.jenkins.io/redirect/security-144/");
            return false;
        }
    }