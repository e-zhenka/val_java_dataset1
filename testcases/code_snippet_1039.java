@GET
    @Path("/purge/{date}")
    public void purge(@PathParam("date") String date) {
        try {
            clusterService.purge(new SimpleDateFormat("yyyy-MM-dd").parse(date));
        } catch (ParseException e) {
            logger.error("Cannot purge",e);
        }
    }