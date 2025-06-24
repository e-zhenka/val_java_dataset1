@RequestMapping("/oauth/token/revoke/client/{clientId}")
    public ResponseEntity<Void> revokeTokensForClient(@PathVariable String clientId) {
        logger.debug("Revoking tokens for client: " + clientId);
        BaseClientDetails client = (BaseClientDetails)clientDetailsService.loadClientByClientId(clientId);
        client.addAdditionalInformation(ClientConstants.TOKEN_SALT,generator.generate());
        clientDetailsService.updateClientDetails(client);
        logger.debug("Tokens revoked for client: " + clientId);
        return new ResponseEntity<>(OK);
    }