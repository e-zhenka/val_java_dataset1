public static String compileMustache(Map<String, Object> context, String template) {
		return compileMustache(context, template, false);
	}