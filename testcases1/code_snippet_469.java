@Parameterized.Parameters(name = "{index}: user[{3}], pwd[{4}]")
    public static Collection<Object[]> parameters() {
        List<Object[]> parameterSets = new ArrayList<>();
        for (String roleSearch : new String[] { ROLE_SEARCH_A, ROLE_SEARCH_B }) {
            addUsers(USER_PATTERN, null, null, roleSearch, parameterSets);
            addUsers(null, USER_SEARCH, USER_BASE, roleSearch, parameterSets);
        }
        return parameterSets;
    }