private String getBodyContentAsString() {
		if (this.body == null) {
			return null;
		}
		try {
			String contentType = (this.messageProperties != null) ? this.messageProperties.getContentType() : null;
			if (MessageProperties.CONTENT_TYPE_SERIALIZED_OBJECT.equals(contentType)) {
				return SERIALIZER_MESSAGE_CONVERTER.fromMessage(this).toString();
			}
			if (MessageProperties.CONTENT_TYPE_TEXT_PLAIN.equals(contentType)
					|| MessageProperties.CONTENT_TYPE_JSON.equals(contentType)
					|| MessageProperties.CONTENT_TYPE_JSON_ALT.equals(contentType)
					|| MessageProperties.CONTENT_TYPE_XML.equals(contentType)) {
				return new String(this.body, ENCODING);
			}
		}
		catch (Exception e) {
			// ignore
		}
		// Comes out as '[B@....b' (so harmless)
		return this.body.toString() + "(byte[" + this.body.length + "])"; //NOSONAR
	}