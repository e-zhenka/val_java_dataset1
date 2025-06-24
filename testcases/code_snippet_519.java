public void encode(FacesContext facesContext) throws IOException {
        OutputStream outStream = facesContext.getExternalContext().getResponseOutputStream();
        contentProducer.invoke(facesContext.getELContext(), new Object[] { outStream, userData });
    }