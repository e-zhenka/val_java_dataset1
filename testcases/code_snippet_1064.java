@GET
    @Path("/purge/{date}")
    public void purge(@PathParam("date") String date) {
        try {
            clusterService.purge(new SimpleDateFormat("yyyy-MM-dd").parse(date));
        } catch (ParseException e) {
            logger.error("Cannot parse date, expected format is: yyyy-MM-dd. See debug log level for more information");
            if (logger.isDebugEnabled()) {
                logger.debug("Cannot parse date: {}", date, e);
            }
        }
    }