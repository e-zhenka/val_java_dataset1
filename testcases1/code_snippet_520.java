protected void addDefaultMapping(DefaultMapper mapper, String parameter, Object model) {
		Expression source = new RequestParameterExpression(parameter);
		ParserContext parserContext = new FluentParserContext().evaluate(model.getClass());
		if (expressionParser instanceof BeanWrapperExpressionParser || checkModelProperty(parameter, model)) {
			Expression target = expressionParser.parseExpression(parameter, parserContext);
			DefaultMapping mapping = new DefaultMapping(source, target);
			if (logger.isDebugEnabled()) {
				logger.debug("Adding default mapping for parameter '" + parameter + "'");
			}
			mapper.addMapping(mapping);
		}
	}