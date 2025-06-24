private SSLException checkSSLAlerts() {
        debug("JSSEngine: Checking inbound and outbound SSL Alerts. Have " + ssl_fd.inboundAlerts.size() + " inbound and " + ssl_fd.outboundAlerts.size() + " outbound alerts.");

        // Prefer inbound alerts to outbound alerts.
        while (ssl_fd.inboundOffset < ssl_fd.inboundAlerts.size()) {
            SSLAlertEvent event = ssl_fd.inboundAlerts.get(ssl_fd.inboundOffset);
            ssl_fd.inboundOffset += 1;

            if (event.getLevelEnum() == SSLAlertLevel.WARNING && event.getDescriptionEnum() == SSLAlertDescription.CLOSE_NOTIFY) {
                debug("Got inbound CLOSE_NOTIFY alert");
                closeInbound();
            }

            debug("JSSEngine: Got inbound alert: " + event);

            // Fire inbound alert prior to raising any exception.
            fireAlertReceived(event);

            // Not every SSL Alert is fatal; toException() only returns a
            // SSLException on fatal instances. We shouldn't return NULL
            // early without checking all alerts.
            SSLException exception = event.toException();
            if (exception != null) {
                return exception;
            }
        }

        while (ssl_fd.outboundOffset < ssl_fd.outboundAlerts.size()) {
            SSLAlertEvent event = ssl_fd.outboundAlerts.get(ssl_fd.outboundOffset);
            ssl_fd.outboundOffset += 1;

            if (event.getLevelEnum() == SSLAlertLevel.WARNING && event.getDescriptionEnum() == SSLAlertDescription.CLOSE_NOTIFY) {
                debug("Sent outbound CLOSE_NOTIFY alert.");
                closeOutbound();
            }

            debug("JSSEngine: Got outbound alert: " + event);

            // Fire outbound alert prior to raising any exception. Note that
            // this still triggers after this alert is written to the output
            // wire buffer.
            fireAlertSent(event);

            SSLException exception = event.toException();
            if (exception != null) {
                return exception;
            }
        }

        return null;
    }