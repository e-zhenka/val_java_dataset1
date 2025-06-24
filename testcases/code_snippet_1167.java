private void checkCancelOperationUsingUrl(Function<Queue.Item, String> urlProvider, boolean legacyRedirect) throws Exception {
        Queue q = r.jenkins.getQueue();

        r.jenkins.setCrumbIssuer(null);
        r.jenkins.setSecurityRealm(r.createDummySecurityRealm());
        r.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy()
                .grant(Jenkins.READ, Item.READ, Item.CANCEL).everywhere().to("admin")
                .grant(Jenkins.READ).everywhere().to("user")
        );

        // prevent execution to push stuff into the queue
        r.jenkins.setNumExecutors(0);
        assertThat(q.getItems().length, equalTo(0));

        FreeStyleProject testProject = r.createFreeStyleProject("test");
        testProject.scheduleBuild(new UserIdCause());

        Queue.Item[] items = q.getItems();
        assertThat(items.length, equalTo(1));
        Queue.Item currentOne = items[0];
        assertFalse(currentOne.getFuture().isCancelled());

        WebRequest request = new WebRequest(new URL(r.getURL() + urlProvider.apply(currentOne)), HttpMethod.POST);

        { // user without right cannot cancel
            JenkinsRule.WebClient wc = r.createWebClient()
                    .withRedirectEnabled(false)
                    .withThrowExceptionOnFailingStatusCode(false);
            wc.login("user");
            if(legacyRedirect) {
                Page p = wc.getPage(request);
                // the legacy endpoint returns a redirection to the previously visited page, none in our case
                // (so force no redirect to avoid false positive error)
                // see JENKINS-21311
                assertThat(p.getWebResponse().getStatusCode(), lessThan(400));
            }
            assertFalse(currentOne.getFuture().isCancelled());
        }
        { // user with right can
            JenkinsRule.WebClient wc = r.createWebClient()
                    .withRedirectEnabled(false)
                    .withThrowExceptionOnFailingStatusCode(false);
            wc.login("admin");
            Page p = wc.getPage(request);
            assertThat(p.getWebResponse().getStatusCode(), lessThan(400));

            assertTrue(currentOne.getFuture().isCancelled());
        }
    }