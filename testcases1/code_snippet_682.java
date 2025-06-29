private static AbstractVariableEvaluationContextPostProcessor createPostProcessor(
			Object request) {
		if (request instanceof AntPathRequestMatcher) {
			return new AntPathMatcherEvaluationContextPostProcessor(
					(AntPathRequestMatcher) request);
		}
		if (request instanceof RequestVariablesExtractor) {
			return new RequestVariablesExtractorEvaluationContextPostProcessor(
					(RequestVariablesExtractor) request);
		}
		return null;
	}