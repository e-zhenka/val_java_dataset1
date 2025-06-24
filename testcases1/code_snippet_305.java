@Restricted(NoExternalUse.class)
    @RequirePOST public HttpResponse doCheckUpdatesServer() throws IOException {
        for (UpdateSite site : Jenkins.getInstance().getUpdateCenter().getSites()) {
            FormValidation v = site.updateDirectlyNow(DownloadService.signatureCheck);
            if (v.kind != FormValidation.Kind.OK) {
                // TODO crude but enough for now
                return v;
            }
        }
        for (DownloadService.Downloadable d : DownloadService.Downloadable.all()) {
            FormValidation v = d.updateNow();
            if (v.kind != FormValidation.Kind.OK) {
                return v;
            }
        }
        return HttpResponses.forwardToPreviousPage();
    }