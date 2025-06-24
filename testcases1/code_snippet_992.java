private ServerWebExchange mapExchange(ServerWebExchange exchange, String methodParamValue) {
		HttpMethod httpMethod = HttpMethod.resolve(methodParamValue.toUpperCase(Locale.ENGLISH));
		Assert.notNull(httpMethod, () -> "HttpMethod '" + methodParamValue + "' not supported");
		if (ALLOWED_METHODS.contains(httpMethod)) {
			return exchange.mutate().request(builder -> builder.method(httpMethod)).build();
		}
		else {
			return exchange;
		}
	}