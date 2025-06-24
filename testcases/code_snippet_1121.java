@Override
    public Object handleEvent(Map<String, String> headers, Object body, int instance) throws AppException, IOException {

        String id = headers.get("id");
        if (id == null) {
            throw new IllegalArgumentException("Missing parameter 'id'");
        }

        if (id.equals("1")) {
            // to set status, key-values or parametric types, we can use EventEnvelope as a result wrapper
            EventEnvelope result = new EventEnvelope();

            ObjectWithGenericType<SamplePoJo> genericObject = new ObjectWithGenericType<>();
            // return some place-holder values to demonstrate the PoJo can be transported over the network
            SamplePoJo mock = new SamplePoJo(1, "Class with generic type resolved at run-time to be SamplePoJo", "200 World Blvd, Planet Earth");
            // set current timestamp to indicate that the object is a new one
            mock.setDate(new Date());
            // set instance count and service origin ID to show that the object comes from a different instance
            mock.setInstance(instance);
            mock.setOrigin(Platform.getInstance().getOrigin());

            genericObject.setId(101);
            genericObject.setContent(mock);

            result.setBody(genericObject);
            result.setParametricType(SamplePoJo.class);

            return result;
        } else {
            throw new AppException(404, "Not found. Try id = 1");
        }

    }