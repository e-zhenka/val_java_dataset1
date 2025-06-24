private void sendEntityMessage(Object message) throws Exception {
        
        MockEndpoint endpoint = getMockEndpoint("mock:result");
        endpoint.reset();
        endpoint.expectedMessageCount(1);
        
        template.sendBody("direct:start1", message);

        assertMockEndpointsSatisfied();
        
        List<Exchange> list = endpoint.getReceivedExchanges();
        Exchange exchange = list.get(0);
        String xml = exchange.getIn().getBody(String.class);
        assertTrue("Get a wrong transformed message", xml.indexOf("<transformed subject=\"\">") > 0);

        endpoint.reset();
        endpoint.expectedMessageCount(1);
        
        try {
            template.sendBody("direct:start2", message);
            list = endpoint.getReceivedExchanges();
            exchange = list.get(0);
            xml = exchange.getIn().getBody(String.class);
            assertTrue("Get a wrong transformed message", xml.indexOf("<transformed subject=\"\">") > 0);
        } catch (Exception ex) {
            // expect an exception here
            assertTrue("Get a wrong exception", ex instanceof CamelExecutionException);
            // the file could not be found
            assertTrue("Get a wrong exception cause", ex.getCause() instanceof TransformerException);
        }
    }