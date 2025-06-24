public TimelineEventList doData(StaplerRequest req, @QueryParameter long min, @QueryParameter long max) throws IOException {
        TimelineEventList result = new TimelineEventList();
        for (Run r : builds.byTimestamp(min,max)) {
            Event e = new Event();
            e.start = new Date(r.getStartTimeInMillis());
            e.end   = new Date(r.getStartTimeInMillis()+r.getDuration());
            // due to SimileAjax.HTML.deEntify (in simile-ajax-bundle.js), "&lt;" are transformed back to "<", but not the "&#60";
            // to protect against XSS
            e.title = Util.escape(r.getFullDisplayName()).replace("&lt;", "&#60;");
            // what to put in the description?
            // e.description = "Longish description of event "+r.getFullDisplayName();
            // e.durationEvent = true;
            e.link = req.getContextPath()+'/'+r.getUrl();
            BallColor c = r.getIconColor();
            e.color = String.format("#%06X",c.getBaseColor().darker().getRGB()&0xFFFFFF);
            e.classname = "event-"+c.noAnime().toString()+" " + (c.isAnimated()?"animated":"");
            result.add(e);
        }
        return result;
    }