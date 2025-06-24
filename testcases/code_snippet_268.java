private HttpServletRequest createMockRequest(String path) {
        HttpServletRequest request = createNiceMock(HttpServletRequest.class);

        expect(request.getServletPath()).andReturn("");
        expect(request.getPathInfo()).andReturn(path);
        replay(request);
        return request;
    }