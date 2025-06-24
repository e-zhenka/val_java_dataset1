public static ListBoxModel doFillCredentialsIdItems(ItemGroup context) {
        AbstractIdCredentialsListBoxModel result = new StandardListBoxModel().includeEmptyValue();
        if (hasPermission(context)) {
            result = result.withMatching(
                            CredentialsMatchers.always(),
                            CredentialsProvider.lookupCredentials(AmazonWebServicesCredentials.class,
                                    context,
                                    ACL.SYSTEM,
                                    Collections.EMPTY_LIST));
        }
        return result;
    }