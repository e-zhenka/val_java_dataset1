@Override
    public void testIsXXEVulnerable() throws Exception {
        try {
            super.testIsXXEVulnerable();
            fail("Thrown " + XStreamException.class.getName() + " expected");
        } catch (final XStreamException e) {
            final String message = e.getMessage().toLowerCase();
            if (message.contains("Package")) {
                throw e;
            }
        }
    }