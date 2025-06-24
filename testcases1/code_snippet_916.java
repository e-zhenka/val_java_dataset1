public void testInsecureParameters() throws Exception {
        // given
        loadConfigurationProviders(new XWorkConfigurationProvider(), new XmlConfigurationProvider("xwork-param-test.xml"));
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("name", "(#context[\"xwork.MethodAccessor.denyMethodExecution\"]= new " +
                        "java.lang.Boolean(false), #_memberAccess[\"allowStaticMethodAccess\"]= new java.lang.Boolean(true), " +
                        "@java.lang.Runtime@getRuntime().exec('mkdir /tmp/PWNAGE'))(meh)");
                put("top['name'](0)", "true");
            }
        };

        ParametersInterceptor pi = new ParametersInterceptor();
        container.inject(pi);
        ValueStack vs = ActionContext.getContext().getValueStack();

        // when
        ValidateAction action = new ValidateAction();
        pi.setParameters(action, vs, params);

        // then
        assertEquals(2, action.getActionMessages().size());

        String msg1 = action.getActionMessage(0);
        String msg2 = action.getActionMessage(1);

        assertEquals("Error setting expression 'name' with value '(#context[\"xwork.MethodAccessor.denyMethodExecution\"]= new java.lang.Boolean(false), #_memberAccess[\"allowStaticMethodAccess\"]= new java.lang.Boolean(true), @java.lang.Runtime@getRuntime().exec('mkdir /tmp/PWNAGE'))(meh)'", msg1);
        assertEquals("Error setting expression 'top['name'](0)' with value 'true'", msg2);
        assertNull(action.getName());
    }