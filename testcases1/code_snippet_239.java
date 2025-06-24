private LikeCondition createLike(Type type, String toMatch) {
		if (notLike) {
			return new NotLikeCondition(selector, selector.generateParameter(type.wrap(toMatch)));
		} else {
			return new LikeCondition(selector, selector.generateParameter(type.wrap(toMatch)));
		}
	}