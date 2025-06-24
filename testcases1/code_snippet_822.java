private boolean isSensitiveValue(ModelNode value) {
            if (value.getType() == ModelType.EXPRESSION
                    || value.getType() == ModelType.STRING) {
                String valueString = value.asString();
                return VAULT_EXPRESSION_PATTERN.matcher(valueString).matches();
            }
            return false;
        }