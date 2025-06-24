public static String substituteEnv(String s) {
        return replaceMacro(s, System.getenv());
    }