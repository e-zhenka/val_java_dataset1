public static ListBoxModel doFillCredentialsIdItems(ItemGroup context) {
        return new StandardListBoxModel()
                .withEmptySelection()
                .withMatching(
                        CredentialsMatchers.always(),
                        CredentialsProvider.lookupCredentials(AmazonWebServicesCredentials.class,
                                context,
                                ACL.SYSTEM,
                                Collections.EMPTY_LIST));
    }