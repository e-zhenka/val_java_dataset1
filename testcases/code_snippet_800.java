@Deprecated
    public static String substituteEnv(String s) {
        return SUBSTITUTE_ENV ? replaceMacro(s, System.getenv()) : s;
    }