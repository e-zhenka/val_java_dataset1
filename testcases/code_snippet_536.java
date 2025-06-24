static Set<String> keysWithVariableValues(Map<String, String> rawConfig, Pattern pattern) {
        Set<String> keys = new HashSet<>();
        for (Map.Entry<String, String> config : rawConfig.entrySet()) {
            if (config.getValue() != null) {
                Matcher matcher = pattern.matcher(config.getValue());
                if (matcher.find()) {
                    keys.add(config.getKey());
                }
            }
        }
        return keys;
    }