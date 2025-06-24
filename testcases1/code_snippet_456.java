@Override
    public int setErrorParameter(BeforeEnterEvent event,
            ErrorParameter<NotFoundException> parameter) {
        String path = event.getLocation().getPath();
        String additionalInfo = "";
        if (parameter.hasCustomMessage()) {
            additionalInfo = "Reason: " + parameter.getCustomMessage();
        }
        path = Jsoup.clean(path, Whitelist.none());
        additionalInfo = Jsoup.clean(additionalInfo, Whitelist.none());

        boolean productionMode = event.getUI().getSession().getConfiguration()
                .isProductionMode();

        String template = getErrorHtml(productionMode);
        // {{routes}} should be replaced first so that it's not possible to
        // insert {{routes}} snippet via other template values which may result
        // in the listing of all available routes when this shouldn't not happen
        if (template.contains("{{routes}}")) {
            template = template.replace("{{routes}}", getRoutes(event));
        }
        template = template.replace("{{additionalInfo}}", additionalInfo);
        template = template.replace("{{path}}", path);

        getElement().appendChild(new Html(template).getElement());
        return HttpServletResponse.SC_NOT_FOUND;
    }