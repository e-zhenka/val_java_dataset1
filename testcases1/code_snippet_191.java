private String getBodyContentAsString() {
		if (body == null) {
			return null;
		}
		try {
			String contentType = (messageProperties != null) ? messageProperties.getContentType() : null;
			if (MessageProperties.CONTENT_TYPE_SERIALIZED_OBJECT.equals(contentType)) {
				return SERIALIZER_MESSAGE_CONVERTER.fromMessage(this).toString();
			}
			if (MessageProperties.CONTENT_TYPE_TEXT_PLAIN.equals(contentType)
					|| MessageProperties.CONTENT_TYPE_JSON.equals(contentType)
					|| MessageProperties.CONTENT_TYPE_JSON_ALT.equals(contentType)
					|| MessageProperties.CONTENT_TYPE_XML.equals(contentType)) {
				return new String(body, ENCODING);
			}
		}
		catch (Exception e) {
			// ignore
		}
		// Comes out as '[B@....b' (so harmless)
		return body.toString()+"(byte["+body.length+"])";//NOSONAR
	}