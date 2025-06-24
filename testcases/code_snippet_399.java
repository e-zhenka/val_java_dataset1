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
        final String userPCAesBase64 = "0o6DCfePYTjK4q579qzUFEfkeGRvbBOdKHp2y8/nGAltt1Vz8uW0Z8igeO" +
                "Tq/yBmcw25f3Q0ui/Leg3x0iQZWhw9Bbu0mFHmHsGxEd6mPwtUpSegIjyX5c/kZpqnb7QLdajPWiczX8P" +
                "Oc2Eku5+8ye1u38Y8uKlklHxcYCPh0pRiDSBxfjPsLaDfOpGbmPjZd4SVg68i/++TvUjqBNJyb+pDix3f" +
                "PeuPvReWGcE50iovezVZrEfDOAQ0cZYW35ShypMWOmE9yZnb+p8++StDyAUegryyuIa4pjuRzfMh9D+sN" +
                "F9tm/EnDC1VCer2S/a0AGlWAQiM7jrWt1sNinZcKIrvShaWI21tONJt8WhozNS2H72lk4p92rfLNHeglT" +
                "xObxIYxLfTI9KiToSe1nYmpQmbBO8x1wWDkWBG//EqRvhgbIfQVqJp12T0fJC1nFuZuVhw/ZanaAZGDk8" +
                "7aLMiw3T6FBZtWaspgvfH+0TJrTD8Ra386ekNXNN8JW8=";

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