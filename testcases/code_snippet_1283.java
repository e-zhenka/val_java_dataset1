@Override
    public ConsoleAnnotator annotate(Object context, MarkupText text, int charPos) {
        String url = this.url;
        if (url.startsWith("/")) {
            StaplerRequest req = Stapler.getCurrentRequest();
            if (req!=null) {
                // if we are serving HTTP request, we want to use app relative URL
                url = req.getContextPath()+url;
            } else {
                // otherwise presumably this is rendered for e-mails and other non-HTTP stuff
                url = Jenkins.get().getRootUrl()+url.substring(1);
            }
        }
        text.addMarkup(charPos, charPos + length, "<a href='" + Util.escape(url) + "'"+extraAttributes()+">", "</a>");
        return null;
    }