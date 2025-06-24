public String filter(String expression) {
        if (forbiddenExpressionPatterns != null && expressionMatches(expression, forbiddenExpressionPatterns)) {
            logger.warn("Expression filtered because forbidden. See debug log level for more information");
            if (logger.isDebugEnabled()) {
                logger.debug("Expression {} is forbidden by expression filter", expression);
            }

            return null;
        }
        if (allowedExpressionPatterns != null && !expressionMatches(expression, allowedExpressionPatterns)) {
            logger.warn("Expression filtered because not allowed. See debug log level for more information");
            if (logger.isDebugEnabled()) {
                logger.debug("Expression {} is not allowed by expression filter", expression);
            }

            return null;
        }
        return expression;
    }