public Map<String, ResultTypeConfig> getResultTypesByExtension(PackageConfig packageConfig) {
        Map<String, ResultTypeConfig> results = packageConfig.getAllResultTypeConfigs();

        Map<String, ResultTypeConfig> resultsByExtension = new HashMap<String, ResultTypeConfig>();
        resultsByExtension.put("jsp", results.get("dispatcher"));
        resultsByExtension.put("jspf", results.get("dispatcher"));
        resultsByExtension.put("jspx", results.get("dispatcher"));
        resultsByExtension.put("vm", results.get("velocity"));
        resultsByExtension.put("ftl", results.get("freemarker"));
        resultsByExtension.put("html", results.get("dispatcher"));
        resultsByExtension.put("htm", results.get("dispatcher"));
        return resultsByExtension;
    }