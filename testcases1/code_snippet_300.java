@SuppressWarnings("unchecked")
    private static Object parseParameter(Map<String, Object> context, Object value) {
        if (value instanceof String) {
            if (((String) value).startsWith("parameter::") || ((String) value).startsWith("script::")) {
                String s = (String) value;
                if (s.startsWith("parameter::")) {
                    return context.get(StringUtils.substringAfter(s, "parameter::"));
                } else if (s.startsWith("script::")) {
                    String script = StringUtils.substringAfter(s, "script::");
                    return executeScript(context, script);
                }
            }
        } else if (value instanceof Map) {
            Map<String, Object> values = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) value).entrySet()) {
                Object parameter = parseParameter(context, entry.getValue());
                if (parameter == null) {
                    return null;
                }
                values.put(entry.getKey(), parameter);
            }
            return values;
        } else if (value instanceof List) {
            List<Object> values = new ArrayList<Object>();
            for (Object o : ((List<?>) value)) {
                Object parameter = parseParameter(context, o);
                if (parameter != null) {
                    values.add(parameter);
                }
            }
            return values;
        }
        return value;
    }