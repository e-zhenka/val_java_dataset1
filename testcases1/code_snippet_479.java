public void testBasics() throws Exception {
        jenkins.setSecurityRealm(createDummySecurityRealm());
        User u = User.get("foo");
        ApiTokenProperty t = u.getProperty(ApiTokenProperty.class);
        final String token = t.getApiToken();

        // make sure the UI shows the token
        HtmlPage config = createWebClient().goTo(u.getUrl() + "/configure");
        HtmlForm form = config.getFormByName("config");
        assertEquals(token, form.getInputByName("_.apiToken").getValueAttribute());

        // round-trip shouldn't change the API token
        submit(form);
        assertSame(t, u.getProperty(ApiTokenProperty.class));

        WebClient wc = createWebClient();
        wc.setCredentialsProvider(new CredentialsProvider() {
            public Credentials getCredentials(AuthScheme scheme, String host, int port, boolean proxy) throws CredentialsNotAvailableException {
                return new UsernamePasswordCredentials("foo", token);
            }
        });
        wc.setWebConnection(new HttpWebConnection(wc) {
            @Override
            protected HttpClient getHttpClient() {
                HttpClient c = super.getHttpClient();
                c.getParams().setAuthenticationPreemptive(true);
                c.getState().setCredentials(new AuthScope("localhost", localPort, AuthScope.ANY_REALM), new UsernamePasswordCredentials("foo", token));
                return c;
            }
        });

        // test the authentication
        assertEquals(u,wc.executeOnServer(new Callable<User>() {
            public User call() throws Exception {
                return User.current();
            }
        }));
    }