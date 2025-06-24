private boolean isSensitiveValue(ModelNode value) {
            if (value.getType() == ModelType.EXPRESSION
                    || value.getType() == ModelType.STRING) {
                String valueString = value.asString();
                if (ExpressionResolver.EXPRESSION_PATTERN.matcher(valueString).matches()) {
                    int start = valueString.indexOf("${") + 2;
                    int end = valueString.indexOf("}", start);
                    valueString = valueString.substring(start, end);
                    return VaultReader.STANDARD_VAULT_PATTERN.matcher(valueString).matches();
                }
            }
            return false;
        }