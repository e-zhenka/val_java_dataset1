private static AbstractVariableEvaluationContextPostProcessor createPostProcessor(
			Object request) {
		if (request instanceof AntPathRequestMatcher) {
			return new AntPathMatcherEvaluationContextPostProcessor(
					(AntPathRequestMatcher) request);
		}
		return null;
	}