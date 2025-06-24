public static void enableDefaultTyping(ObjectMapper mapper) {
		if(mapper != null) {
			TypeResolverBuilder<?> typeBuilder = mapper.getDeserializationConfig().getDefaultTyper(null);
			if (typeBuilder == null) {
				mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
			}
		}
	}