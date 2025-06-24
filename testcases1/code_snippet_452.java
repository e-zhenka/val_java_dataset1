public void update(long done, long total, int item) {
    if (exceptionTrhown) { return; }

    // To avoid cache overloading, this object is saved when the upload starts,
    // when it has finished, or when the interval from the last save is significant.
    boolean save = bytesRead == 0 && done > 0 || done >= total || (new Date()).getTime() - saved.getTime() > DEFAULT_SAVE_INTERVAL;
    bytesRead = done;
    contentLength = total;
    if (save) {
      save();
    }

    // If other request has set an exception, it is thrown so the commons-fileupload's
    // parser stops and the connection is closed.
    if (isCanceled()) {
      String eName = exception.getClass().getName().replaceAll("^.+\\.", "");
      logger.info(className + " " + sessionId + " The upload has been canceled after " + bytesRead + " bytes received, raising an exception (" + eName + ") to close the socket");
      exceptionTrhown = true;
      throw exception;
    }

    // Just a way to slow down the upload process and see the progress bar in fast networks.
    if (slowUploads > 0 && done < total) {
      try {
        Thread.sleep(slowUploads);
      } catch (Exception e) {
        exception = new RuntimeException(e);
      }
    }
  }