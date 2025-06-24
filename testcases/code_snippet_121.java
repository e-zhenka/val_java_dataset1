private LikeCondition createLike(Type type, String toMatch) {
		if (notLike) {
			return new NotLikeCondition(type, selector, toMatch);
		} else {
			return new LikeCondition(type, selector, toMatch);
		}
	}