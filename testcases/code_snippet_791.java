public static Map<String, String> getSAMLAttributes() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(SAML_CLIENT_SIGNATURE, "true");
        attributes.put(SAML_AUTHNSTATEMENT, "true");
        attributes.put(SAML_FORCE_POST_BINDING, "true");
        attributes.put(SAML_SERVER_SIGNATURE, "true");
        attributes.put(SAML_SIGNATURE_ALGORITHM, "RSA_SHA256");
        attributes.put(SAML_FORCE_NAME_ID_FORMAT, "false");
        attributes.put(SAML_NAME_ID_FORMAT, "username");
        attributes.put(SamlConfigAttributes.SAML_ARTIFACT_BINDING_IDENTIFIER, ArtifactBindingUtils.computeArtifactBindingIdentifierString("saml"));
        return attributes;
    }