private Document getFragmentAsDocument(CharSequence value) {
		// using the XML parser ensures that all elements in the input are retained, also if they actually are not allowed at the given
		// location; E.g. a <td> element isn't allowed directly within the <body> element, so it would be used by the default HTML parser.
		// we need to retain it though to apply the given white list properly; See HV-873
		Document fragment = Jsoup.parse( value.toString(), baseURI, Parser.xmlParser() );
		Document document = Document.createShell( baseURI );

		// add the fragment's nodes to the body of resulting document
		List<Node> childNodes = fragment.childNodes();
		for ( Node node : childNodes ) {
			document.body().appendChild( node.clone() );
		}

		return document;
	}