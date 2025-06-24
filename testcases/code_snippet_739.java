@Test(expected = CryptoException.class)
    public void getRememberedPrincipalsNoMoreDefaultCipher() {
        HttpServletRequest mockRequest = createMock(HttpServletRequest.class);
        HttpServletResponse mockResponse = createMock(HttpServletResponse.class);
        WebSubjectContext context = new DefaultWebSubjectContext();
        context.setServletRequest(mockRequest);
        context.setServletResponse(mockResponse);

        expect(mockRequest.getAttribute(ShiroHttpServletRequest.IDENTITY_REMOVED_KEY)).andReturn(null);
        expect(mockRequest.getContextPath()).andReturn( "/test" );


        //The following base64 string was determined from the log output of the above 'onSuccessfulLogin' test.
        //This will have to change any time the PrincipalCollection implementation changes:
        final String userPCAesBase64 = "WlD5MLzzZznN3dQ1lPJO/eScSuY245k29aECNmjUs31o7Yu478hWhaM5Sj" +
            "jmoe900/72JNu3hcJaPG6Q17Vuz4F8x0kBjbFnPVx4PqzsZYT6yreeS2jwO6OwfI+efqXOKyB2a5KPtnr" +
            "7jt5kZsyH38XJISb81cf6xqTGUru8zC+kNqJFz7E5RpO0kraBofS5jhMm45gDVjDRkjgPJAzocVWMtrza" +
            "zy67P8eb+kMSBCqGI251JTNAGboVgQ28KjfaAJ/6LXRJUj7kB7CGia7mgRk+hxzEJGDs81at5VOPqODJr" +
            "xb8tcIdemFUFIkiYVP9bGs4dP3ECtmw7aNrCzv+84sx3vRFUrd5DbDYpEuE12hF2Y9owDK9sxStbXoF0y" +
            "A32dhfGDIqS+agsass0sWn8WX2TM9i8SxrUjiFbxqyIG49HbqGrZp5QLM9IuIwO+TzGfF1FzumQGdwmWT" +
            "xkVapw5UESl34YvA615cb+82ue1I=";

        Cookie[] cookies = new Cookie[]{
            new Cookie(CookieRememberMeManager.DEFAULT_REMEMBER_ME_COOKIE_NAME, userPCAesBase64)
        };

        expect(mockRequest.getCookies()).andReturn(cookies);
        replay(mockRequest);

        CookieRememberMeManager mgr = new CookieRememberMeManager();
        // without the old default cipher set, this will fail (expected)
        // mgr.setCipherKey( Base64.decode("kPH+bIxk5D2deZiIxcaaaA=="));
        // this will throw a CryptoException
        mgr.getRememberedPrincipals(context);
    }