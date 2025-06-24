public void testCookiesWithClassPollution() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String pollution1 = "model['class']['classLoader']['jarPath']";
        String pollution2 = "model.class.classLoader.jarPath";
        String pollution3 = "class.classLoader.jarPath";
        String pollution4 = "class['classLoader']['jarPath']";
        String pollution5 = "model[\"class\"]['classLoader']['jarPath']";
        String pollution6 = "class[\"classLoader\"]['jarPath']";

        request.setCookies(
                new Cookie(pollution1, "pollution1"),
                new Cookie("pollution1", pollution1),
                new Cookie(pollution2, "pollution2"),
                new Cookie("pollution2", pollution2),
                new Cookie(pollution3, "pollution3"),
                new Cookie("pollution3", pollution3),
                new Cookie(pollution4, "pollution4"),
                new Cookie("pollution4", pollution4),
                new Cookie(pollution5, "pollution5"),
                new Cookie("pollution5", pollution5),
                new Cookie(pollution6, "pollution6"),
                new Cookie("pollution6", pollution6)
            );
        ServletActionContext.setRequest(request);

        final Map<String, Boolean> excludedName = new HashMap<String, Boolean>();
        final Map<String, Boolean> excludedValue = new HashMap<String, Boolean>();

        CookieInterceptor interceptor = new CookieInterceptor() {
            @Override
            protected boolean isAcceptableName(String name) {
                boolean accepted = super.isAcceptableName(name);
                excludedName.put(name, accepted);
                return accepted;
            }

            @Override
            protected boolean isAcceptableValue(String value) {
                boolean accepted = super.isAcceptableValue(value);
                excludedValue.put(value, accepted);
                return accepted;
            }
        };
        DefaultExcludedPatternsChecker excludedPatternsChecker = new DefaultExcludedPatternsChecker();
        excludedPatternsChecker.setAdditionalExcludePatterns(".*(^|\\.|\\[|'|\")class(\\.|\\[|'|\").*");
        interceptor.setExcludedPatternsChecker(excludedPatternsChecker);
        interceptor.setCookiesName("*");

        MockActionInvocation invocation = new MockActionInvocation();
        invocation.setAction(new MockActionWithCookieAware());

        interceptor.intercept(invocation);

        assertFalse(excludedName.get(pollution1));
        assertFalse(excludedName.get(pollution2));
        assertFalse(excludedName.get(pollution3));
        assertFalse(excludedName.get(pollution4));
        assertFalse(excludedName.get(pollution5));
        assertFalse(excludedName.get(pollution6));

        assertFalse(excludedValue.get(pollution1));
        assertFalse(excludedValue.get(pollution2));
        assertFalse(excludedValue.get(pollution3));
        assertFalse(excludedValue.get(pollution4));
        assertFalse(excludedValue.get(pollution5));
        assertFalse(excludedValue.get(pollution6));
    }