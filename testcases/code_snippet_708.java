public static String[] validateObject(ParaObject content) {
		if (content == null) {
			return new String[]{"Object cannot be null."};
		}
		LinkedList<String> list = new LinkedList<>();
		try {
			for (ConstraintViolation<ParaObject> constraintViolation : getValidator().validate(content)) {
				String prop = "'".concat(constraintViolation.getPropertyPath().toString()).concat("'");
				list.add(prop.concat(" ").concat(constraintViolation.getMessage()));
			}
			if (content instanceof User && StringUtils.length(((User) content).getPassword()) > User.MAX_PASSWORD_LENGTH) {
				list.add(Utils.formatMessage("{0} must not be longer than {1}.", Config._PASSWORD, User.MAX_PASSWORD_LENGTH));
			}
		} catch (Exception e) {
			logger.error(null, e);
		}
		return list.toArray(new String[]{});
	}