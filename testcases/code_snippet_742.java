@Override
    public void validate() {
        final String filter = FilterEncoder.format(ldapConfiguration.getUserSearchFilter(), "test");
        ldapConnectionTemplate.searchFirst(ldapConfiguration.getSearchBases().get(0), filter, SearchScope.SUBTREE, entry -> entry);
    }