@RequestMapping("/oauth/token/revoke/client/{clientId}")
    public ResponseEntity<Void> revokeTokensForClient(@PathVariable String clientId) {
        logger.debug("Revoking tokens for client: " + clientId);
        String zoneId = IdentityZoneHolder.get().getId();
        BaseClientDetails client = (BaseClientDetails)clientDetailsService.loadClientByClientId(clientId, zoneId);
        client.addAdditionalInformation(ClientConstants.TOKEN_SALT,generator.generate());
        clientDetailsService.updateClientDetails(client, zoneId);
        logger.debug("Tokens revoked for client: " + clientId);
        ((SystemDeletable)tokenProvisioning).deleteByClient(clientId, zoneId);
        return new ResponseEntity<>(OK);
    }