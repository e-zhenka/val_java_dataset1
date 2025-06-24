@RequestMapping("/oauth/token/revoke/client/{clientId}")
    public ResponseEntity<Void> revokeTokensForClient(@PathVariable String clientId) {
        logger.debug("Revoking tokens for client: " + clientId);
        BaseClientDetails client = (BaseClientDetails)clientDetailsService.loadClientByClientId(clientId, IdentityZoneHolder.get().getId());
        client.addAdditionalInformation(ClientConstants.TOKEN_SALT,generator.generate());
        clientDetailsService.updateClientDetails(client, IdentityZoneHolder.get().getId());
        logger.debug("Tokens revoked for client: " + clientId);
        return new ResponseEntity<>(OK);
    }