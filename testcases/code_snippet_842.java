@PresetData(DataSet.NO_ANONYMOUS_READACCESS)
    @SuppressWarnings("SleepWhileInLoop")
    public void testServiceUsingDirectSecret() throws Exception {
        Slave slave = createNewJnlpSlave("test");
        jenkins.setNodes(Collections.singletonList(slave));
        new WebClient().goTo("computer/test/slave-agent.jnlp?encrypt=true", "application/octet-stream");
        String secret = slave.getComputer().getJnlpMac();
        // To watch it fail: secret = secret.replace('1', '2');
        ProcessBuilder pb = new ProcessBuilder(JavaEnvUtils.getJreExecutable("java"), "-jar", Which.jarFile(Launcher.class).getAbsolutePath(), "-jnlpUrl", getURL() + "computer/test/slave-agent.jnlp", "-secret", secret);
        try {
            pb = (ProcessBuilder) ProcessBuilder.class.getMethod("inheritIO").invoke(pb);
        } catch (NoSuchMethodException x) {
            // prior to Java 7
        }
        System.err.println("Running: " + pb.command());
        Process p = pb.start();
        try {
            for (int i = 0; i < /* one minute */600; i++) {
                if (slave.getComputer().isOnline()) {
                    System.err.println("JNLP slave successfully connected");
                    return;
                }
                Thread.sleep(100);
            }
            fail("JNLP slave agent failed to connect");
        } finally {
            p.destroy();
        }
    }