private void openConnection() {
        if (!connectionOpened) {
            connectionOpened = true;
            URLConnection connection = null;
            try {
                try {
                    connection = url.openConnection();
                } catch (IOException e) {
                    lastModified = null;
                    contentLength = null;
                    return;
                }
                lastModified =  new Date(connection.getLastModified());
                contentLength = connection.getContentLengthLong();
            } finally {
                if (connection != null) {
                    try {
                        IoUtils.safeClose(connection.getInputStream());
                    } catch (IOException e) {
                        //ignore
                    }
                }
            }
        }
    }