public String filter(String expression) {
        if (forbiddenExpressionPatterns != null && expressionMatches(expression, forbiddenExpressionPatterns)) {
            logger.warn("Expression {} is forbidden by expression filter", expression);
            return null;
        }
        if (allowedExpressionPatterns != null && !expressionMatches(expression, allowedExpressionPatterns)) {
            logger.warn("Expression {} is not allowed by expression filter", expression);
            return null;
        }
        return expression;
    }