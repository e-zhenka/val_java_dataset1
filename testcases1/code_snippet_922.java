private void mapAndDelegateIncomingCommand(final String tenantId, final String originalDeviceId,
            final CommandContext originalCommandContext) {
        // note that the command might be invalid here - a matching local handler to reject it (and report metrics) shall be found in that case
        final Command originalCommand = originalCommandContext.getCommand();

        // determine last used gateway device id
        LOG.trace("determine command target gateway/adapter for [{}]", originalCommand);
        final Future<JsonObject> commandTargetFuture = commandTargetMapper.getTargetGatewayAndAdapterInstance(tenantId,
                originalDeviceId, originalCommandContext.getTracingContext());

        commandTargetFuture.onComplete(commandTargetResult -> {
            if (commandTargetResult.succeeded()) {
                final String targetDeviceId = commandTargetResult.result().getString(DeviceConnectionConstants.FIELD_PAYLOAD_DEVICE_ID);
                final String targetAdapterInstance = commandTargetResult.result().getString(DeviceConnectionConstants.FIELD_ADAPTER_INSTANCE_ID);

                delegateIncomingCommand(tenantId, originalDeviceId, originalCommandContext, targetDeviceId, targetAdapterInstance);

            } else {
                if (commandTargetResult.cause() instanceof ServiceInvocationException
                        && ((ServiceInvocationException) commandTargetResult.cause()).getErrorCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    LOG.debug("no target adapter instance found for command for device {}", originalDeviceId);
                    TracingHelper.logError(originalCommandContext.getTracingSpan(),
                            "no target adapter instance found for command with device id " + originalDeviceId);
                } else {
                    LOG.debug("error getting target gateway and adapter instance for command with device id {}",
                            originalDeviceId, commandTargetResult.cause());
                    TracingHelper.logError(originalCommandContext.getTracingSpan(),
                            "error getting target gateway and adapter instance for command with device id " + originalDeviceId,
                            commandTargetResult.cause());
                }
                originalCommandContext.release();
            }
        });
    }