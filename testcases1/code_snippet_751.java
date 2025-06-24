private Exception doRequest() {

            Tomcat tomcat = getTomcatInstance();

            Context root = tomcat.addContext("", TEMP_DIR);
            Tomcat.addServlet(root, "Bug51557",
                    new Bug51557Servlet(headerName));
            root.addServletMapping("/test", "Bug51557");

            try {
                Connector connector = tomcat.getConnector();
                connector.setProperty("rejectIllegalHeaderName",
                        Boolean.toString(rejectIllegalHeaderName));
                tomcat.start();
                setPort(connector.getLocalPort());

                // Open connection
                connect();

                String[] request = new String[1];
                request[0] =
                    "GET /test HTTP/1.1" + CRLF +
                    "host: localhost:8080" + CRLF +
                    headerLine + CRLF +
                    "X-Bug51557: abcd" + CRLF +
                    "Connection: close" + CRLF +
                    CRLF;

                setRequest(request);
                processRequest(); // blocks until response has been read

                // Close the connection
                disconnect();
            } catch (Exception e) {
                return e;
            }
            return null;
        }