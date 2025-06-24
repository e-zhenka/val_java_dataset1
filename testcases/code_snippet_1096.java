@Override
    public void initialize(ScriptContext scriptContext)
    {
        XWikiContext xcontext = this.xcontextProvider.get();

        if (scriptContext.getAttribute("util") == null) {
            // Put the Util API in the Script context.
            scriptContext.setAttribute("util", new com.xpn.xwiki.api.Util(xcontext.getWiki(), xcontext),
                ScriptContext.ENGINE_SCOPE);

            // We put the com.xpn.xwiki.api.XWiki object into the context and not the com.xpn.xwiki.XWiki one which is
            // for internal use only. In this manner we control what the user can access.
            scriptContext.setAttribute("xwiki", new XWiki(xcontext.getWiki(), xcontext), ScriptContext.ENGINE_SCOPE);

            scriptContext.setAttribute("request",
                new ScriptXWikiServletRequest(xcontext.getRequest(), this.authorization), ScriptContext.ENGINE_SCOPE);
            scriptContext.setAttribute("response", xcontext.getResponse(), ScriptContext.ENGINE_SCOPE);

            // We put the com.xpn.xwiki.api.Context object into the context and not the com.xpn.xwiki.XWikiContext one
            // which is for internal use only. In this manner we control what the user can access.
            // We use "xcontext" because "context" is a reserved binding in JSR-223 specifications
            scriptContext.setAttribute("xcontext", new Context(xcontext), ScriptContext.ENGINE_SCOPE);
        }

        // Current document
        Document docAPI = null;
        XWikiDocument doc = xcontext.getDoc();
        if (doc != null) {
            docAPI = setDocument(scriptContext, "doc", doc, xcontext);

            XWikiDocument tdoc = (XWikiDocument) xcontext.get("tdoc");
            if (tdoc == null) {
                try {
                    tdoc = doc.getTranslatedDocument(xcontext);
                } catch (XWikiException e) {
                    this.logger.warn("Failed to retrieve the translated document for [{}]. "
                        + "Continue using the default translation.", doc.getDocumentReference(), e);
                    tdoc = doc;
                }
            }
            Document tdocAPI = setDocument(scriptContext, "tdoc", tdoc, xcontext);

            XWikiDocument cdoc = (XWikiDocument) xcontext.get("cdoc");
            if (cdoc == null) {
                Document cdocAPI = tdocAPI;
                if (cdocAPI == null) {
                    cdocAPI = docAPI;
                }
                scriptContext.setAttribute("cdoc", cdocAPI, ScriptContext.ENGINE_SCOPE);
            } else {
                setDocument(scriptContext, "cdoc", cdoc, xcontext);
            }
        }

        // Current secure document
        XWikiDocument sdoc = (XWikiDocument) xcontext.get("sdoc");
        if (sdoc == null) {
            scriptContext.setAttribute("sdoc", docAPI, ScriptContext.ENGINE_SCOPE);
        } else {
            setDocument(scriptContext, "sdoc", sdoc, xcontext);
        }

        // Miscellaneous
        scriptContext.setAttribute("locale", xcontext.getLocale(), ScriptContext.ENGINE_SCOPE);
    }