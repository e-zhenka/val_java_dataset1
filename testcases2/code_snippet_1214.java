public static String getPropertyDef(InputSpec inputSpec, Map<String, Integer> indexes, 
			String pattern, DefaultValueProvider defaultValueProvider) {
		pattern = InputSpec.escape(pattern);
		int index = indexes.get(inputSpec.getName());
		StringBuffer buffer = new StringBuffer();
		inputSpec.appendField(buffer, index, "String");
		inputSpec.appendCommonAnnotations(buffer, index);
		if (!inputSpec.isAllowEmpty())
			buffer.append("    @NotEmpty\n");
		if (pattern != null)
			buffer.append("    @Pattern(regexp=\"" + pattern + "\", message=\"Should match regular expression: " + pattern + "\")\n");
		inputSpec.appendMethods(buffer, index, "String", null, defaultValueProvider);

		return buffer.toString();
	}