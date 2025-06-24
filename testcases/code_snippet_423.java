private HttpServletRequest createMockRequest(String path) {
        HttpServletRequest request = createNiceMock(HttpServletRequest.class);

        expect(request.getAttribute(WebUtils.INCLUDE_CONTEXT_PATH_ATTRIBUTE)).andReturn(null).anyTimes();
        expect(request.getContextPath()).andReturn("");
        expect(request.getPathInfo()).andReturn(path);
        replay(request);
        return request;
    }