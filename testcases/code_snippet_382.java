public int validateUser(boolean withConfirmEmail, XWikiContext context) throws XWikiException
    {
        try {
            XWikiRequest request = context.getRequest();
            // Get the user document
            String username = convertUsername(request.getParameter("xwikiname"), context);
            if (username.indexOf('.') == -1) {
                username = "XWiki." + username;
            }
            XWikiDocument userDocument = getDocument(username, context);

            // Get the stored validation key
            BaseObject userObject = userDocument.getObject("XWiki.XWikiUsers", 0);
            String storedKey = userObject.getStringValue("validkey");

            // Get the validation key from the URL
            String validationKey = request.getParameter("validkey");
            PropertyInterface validationKeyClass = getClass("XWiki.XWikiUsers", context).get("validkey");
            if (validationKeyClass instanceof PasswordClass) {
                validationKey = ((PasswordClass) validationKeyClass).getEquivalentPassword(storedKey, validationKey);
            }

            // Compare the two keys
            if ((!storedKey.equals("") && (storedKey.equals(validationKey)))) {
                XWikiUser xWikiUser = new XWikiUser(userDocument.getDocumentReference());
                xWikiUser.setDisabled(false, context);
                xWikiUser.setEmailChecked(true, context);
                saveDocument(userDocument, context);

                if (withConfirmEmail) {
                    String email = userObject.getStringValue("email");
                    String password = userObject.getStringValue("password");
                    sendValidationEmail(username, password, email, request.getParameter("validkey"),
                        "confirmation_email_content", context);
                }

                return 0;
            } else {
                return -1;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);

            throw new XWikiException(XWikiException.MODULE_XWIKI_APP, XWikiException.ERROR_XWIKI_APP_VALIDATE_USER,
                "Exception while validating user", e, null);
        }
    }