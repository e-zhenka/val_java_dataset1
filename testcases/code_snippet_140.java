public void testCall(final String path, final String expectedResponse) throws Exception {
        TestHttpClient client = new TestHttpClient();
        String url = DefaultServer.getDefaultServerURL() + "/servletContext/secured/" + path;
        HttpGet get = new HttpGet(url);
        HttpResponse result = client.execute(get);
        assertEquals(StatusCodes.UNAUTHORIZED, result.getStatusLine().getStatusCode());
        Header[] values = result.getHeaders(WWW_AUTHENTICATE.toString());
        assertEquals(1, values.length);
        String value = values[0].getValue();
        assertTrue(value.startsWith(DIGEST.toString()));
        Map<DigestWWWAuthenticateToken, String> parsedHeader = DigestWWWAuthenticateToken.parseHeader(value.substring(7));
        assertEquals(REALM_NAME, parsedHeader.get(DigestWWWAuthenticateToken.REALM));
        assertEquals(DigestAlgorithm.MD5.getToken(), parsedHeader.get(DigestWWWAuthenticateToken.ALGORITHM));
        assertTrue(parsedHeader.containsKey(DigestWWWAuthenticateToken.MESSAGE_QOP));

        String nonce = parsedHeader.get(DigestWWWAuthenticateToken.NONCE);

        String clientResponse = createResponse("user1", REALM_NAME, "password1", "GET", "/", nonce);

        client = new TestHttpClient();
        get = new HttpGet(url);
        StringBuilder sb = new StringBuilder(DIGEST.toString());
        sb.append(" ");
        sb.append(DigestAuthorizationToken.USERNAME.getName()).append("=").append("\"user1\"").append(",");
        sb.append(DigestAuthorizationToken.REALM.getName()).append("=\"").append(REALM_NAME).append("\",");
        sb.append(DigestAuthorizationToken.NONCE.getName()).append("=\"").append(nonce).append("\",");
        sb.append(DigestAuthorizationToken.DIGEST_URI.getName()).append("=\"/\",");
        sb.append(DigestAuthorizationToken.RESPONSE.getName()).append("=\"").append(clientResponse).append("\"");

        get.addHeader(AUTHORIZATION.toString(), sb.toString());
        result = client.execute(get);
        assertEquals(StatusCodes.OK, result.getStatusLine().getStatusCode());

        final String response = HttpClientUtils.readResponse(result);
        assertEquals(expectedResponse, response);
    }