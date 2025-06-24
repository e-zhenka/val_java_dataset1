private void updateStatus() {
        if (config.getHIDMode() == 0) {
            config.setNetworkStatus(false);
            EditorActivity.stopNetworkSocketService(this);
            ipButton.setVisibility(View.INVISIBLE);
            ipStatusDivider.setVisibility(View.INVISIBLE);
            if (config.getUSBStatus()) {
                statusText.setText(R.string.config_status_usb_on);
                statusImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_usb));
            } else {
                statusText.setText(R.string.config_status_usb_off);
                statusImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_usb_off));
            }
        } else if (config.getHIDMode() == 1) {
            EditorActivity.startNetworkSocketService(this);
            ipButton.setVisibility(View.VISIBLE);
            ipStatusDivider.setVisibility(View.VISIBLE);
            if (config.getNetworkStatus()) {
                statusText.setText(R.string.config_status_net_on);
                statusImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_net));
            } else {
                statusText.setText(R.string.config_status_net_off);
                statusImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_net_off));
            }
            EditorActivity.updateNotification(this);
        }
    }