private void renderState(FacesContext context) throws IOException {
        // Get the view state and write it to the response..
        PartialViewContext pvc = context.getPartialViewContext();
        PartialResponseWriter writer = pvc.getPartialResponseWriter();
        String viewStateId = Util.getViewStateId(context);

        writer.startUpdate(viewStateId);
        String state = context.getApplication().getStateManager().getViewState(context);
        writer.write(state);
        writer.endUpdate();

        ClientWindow window = context.getExternalContext().getClientWindow();
        if (null != window) {
            String clientWindowId = Util.getClientWindowId(context);
            writer.startUpdate(clientWindowId);
            writer.writeText(window.getId(), null);
            writer.endUpdate();
        }
    }