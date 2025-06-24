public void sendFile(String url, Object body, String fileName) {
        template.sendBodyAndHeader(url, body, Exchange.FILE_NAME, simple(fileName));
    }