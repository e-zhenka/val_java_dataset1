public void save(String comment, boolean minorEdit) throws XWikiException
    {
        if (hasAccessLevel("edit")) {
            // If the current author does not have PR don't let it set current user as author of the saved document
            // since it can lead to right escalation
            if (hasProgrammingRights() || !getConfiguration().getProperty("security.script.save.checkAuthor", true)) {
                saveDocument(comment, minorEdit);
            } else {
                saveAsAuthor(comment, minorEdit);
            }
        } else {
            java.lang.Object[] args = {getDefaultEntityReferenceSerializer().serialize(getDocumentReference())};
            throw new XWikiException(XWikiException.MODULE_XWIKI_ACCESS, XWikiException.ERROR_XWIKI_ACCESS_DENIED,
                "Access denied in edit mode on document {0}", null, args);
        }
    }