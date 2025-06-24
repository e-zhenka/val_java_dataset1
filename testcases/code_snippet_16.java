public void recycle() {
        bytesRead=0;

        contentLength = -1;
        contentTypeMB = null;
        charset = null;
        characterEncoding = null;
        expectation = false;
        headers.recycle();
        serverNameMB.recycle();
        serverPort=-1;
        localAddrMB.recycle();
        localNameMB.recycle();
        localPort = -1;
        peerAddrMB.recycle();
        remoteAddrMB.recycle();
        remoteHostMB.recycle();
        remotePort = -1;
        available = 0;
        sendfile = true;

        serverCookies.recycle();
        parameters.recycle();
        pathParameters.clear();

        uriMB.recycle();
        decodedUriMB.recycle();
        queryMB.recycle();
        methodMB.recycle();
        protoMB.recycle();

        schemeMB.recycle();

        remoteUser.recycle();
        remoteUserNeedsAuthorization = false;
        authType.recycle();
        attributes.clear();

        listener = null;
        synchronized (nonBlockingStateLock) {
            fireListener = false;
            registeredForRead = false;
        }
        allDataReadEventSent.set(false);

        startTime = -1;
    }