private ServerWebExchange mapExchange(ServerWebExchange exchange, String methodParamValue) {
		HttpMethod httpMethod = HttpMethod.resolve(methodParamValue.toUpperCase(Locale.ENGLISH));
		Assert.notNull(httpMethod, () -> "HttpMethod '" + methodParamValue + "' not supported");
		return exchange.mutate().request(builder -> builder.method(httpMethod)).build();
	}