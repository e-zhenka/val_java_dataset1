public void encode(FacesContext facesContext) throws IOException {
        OutputStream outStream = facesContext.getExternalContext().getResponseOutputStream();
        String expr = contentProducer.getExpressionString();

        if (!Pattern.matches(PARENTHESES, expr)) { // method expression must not be executed
            throw new IllegalArgumentException("Expression \"" + expr + "\" contains parentheses.");
        }

        contentProducer.invoke(facesContext.getELContext(), new Object[] { outStream, userData });
    }