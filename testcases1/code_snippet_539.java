@Parameterized.Parameters(name = "{index}: user[{3}], pwd[{4}]")
    public static Collection<Object[]> parameters() {
        List<Object[]> parameterSets = new ArrayList<>();
        addUsers(USER_PATTERN, null, null, parameterSets);
        addUsers(null, USER_SEARCH, USER_BASE, parameterSets);
        return parameterSets;
    }