public XWikiRequest getRequest()
    {
        return new ScriptXWikiServletRequest(getXWikiContext().getRequest(), getContextualAuthorizationManager());
    }