public void testBasics() throws Exception {
        jenkins.setSecurityRealm(createDummySecurityRealm());
        User u = User.get("foo");
        final ApiTokenProperty t = u.getProperty(ApiTokenProperty.class);
        final String token = t.getApiToken();

        // Make sure that user is able to get the token via the interface
        ACL.impersonate(u.impersonate(), new Runnable() {
            @Override
            public void run() {
                assertEquals("User is unable to get its own token", token, t.getApiToken());
            }
        });

        // test the authentication via Token
        WebClient wc = createClientForUser("foo");
        assertEquals(u,wc.executeOnServer(new Callable<User>() {
            public User call() throws Exception {
                return User.current();
            }
        }));
        
        // Make sure the UI shows the token to the user
        HtmlPage config = wc.goTo(u.getUrl() + "/configure");
        HtmlForm form = config.getFormByName("config");
        assertEquals(token, form.getInputByName("_.apiToken").getValueAttribute());

        // round-trip shouldn't change the API token
        submit(form);
        assertSame(t, u.getProperty(ApiTokenProperty.class));
    }