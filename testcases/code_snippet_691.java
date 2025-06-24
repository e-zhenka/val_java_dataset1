public static boolean isStaxSource(Source source) {
		return (source instanceof StaxSource || (jaxp14Available && Jaxp14StaxHandler.isStaxSource(source)));
	}