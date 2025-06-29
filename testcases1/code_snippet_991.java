@GetMapping("/callback/{serverId}")
    @ApiOperation(value = "OAuth2.0授权完成后回调", hidden = true)
    public RedirectView callback(@RequestParam(defaultValue = "/") String redirect,
                                 @PathVariable String serverId,
                                 @RequestParam String code,
                                 @RequestParam String state,
                                 HttpServletRequest request,
                                 HttpSession session) throws UnsupportedEncodingException {
        try {
            String cachedState = (String) session.getAttribute(STATE_SESSION_KEY);
            //  if (!state.equals(cachedState)) throw new BusinessException("state error");
            oAuth2RequestService.doEvent(serverId, new OAuth2CodeAuthBeforeEvent(code, state, request::getParameter));
            return new RedirectView(URLDecoder.decode(redirect, "UTF-8"));
        } finally {
            session.removeAttribute(STATE_SESSION_KEY);
        }
    }