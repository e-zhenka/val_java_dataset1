@Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Account account = getRequestedAccount(getZimbraSoapContext(context));

        if (!canAccessAccount(zsc, account))
            throw ServiceException.PERM_DENIED("can not access account");
        
        String name = request.getAttribute(AccountConstants.E_NAME);
        String typeStr = request.getAttribute(AccountConstants.A_TYPE, "account");
        GalSearchType type = GalSearchType.fromString(typeStr);

        boolean needCanExpand = request.getAttributeBool(AccountConstants.A_NEED_EXP, false);

        String galAcctId = request.getAttribute(AccountConstants.A_GAL_ACCOUNT_ID, null);
        
        GalSearchParams params = new GalSearchParams(account, zsc);
        params.setType(type);
        params.setRequest(request);
        params.setQuery(name);
        params.setLimit(account.getContactAutoCompleteMaxResults());
        params.setNeedCanExpand(needCanExpand);
        params.setResponseName(AccountConstants.AUTO_COMPLETE_GAL_RESPONSE);
        if (galAcctId != null)
            params.setGalSyncAccount(Provisioning.getInstance().getAccountById(galAcctId));
        GalSearchControl gal = new GalSearchControl(params);
        gal.autocomplete();
        return params.getResultCallback().getResponse();
    }