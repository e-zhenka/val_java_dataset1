public static String migrate(String xml) {
		Document xmlDoc;
		try {
			xmlDoc = new SAXReader().read(new StringReader(xml));
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		}
		
		List<NodeTuple> tuples = new ArrayList<>();
		Node keyNode = new ScalarNode(Tag.STR, "version");
		Node valueNode = new ScalarNode(Tag.INT, "0");
		tuples.add(new NodeTuple(keyNode, valueNode));
		
		List<Node> jobNodes = new ArrayList<>();
		for (Element jobElement: xmlDoc.getRootElement().element("jobs").elements()) 
			jobNodes.add(migrateJob(jobElement));
		
		if (!jobNodes.isEmpty()) {
			keyNode = new ScalarNode(Tag.STR, "jobs");
			tuples.add(new NodeTuple(keyNode, new SequenceNode(Tag.SEQ, jobNodes, FlowStyle.BLOCK)));
		}
		
		List<Node> propertyNodes = new ArrayList<>();
		Element propertiesElement = xmlDoc.getRootElement().element("properties");
		if (propertiesElement != null) {
			for (Element propertyElement: propertiesElement.elements()) {
				Node nameNode = new ScalarNode(Tag.STR, propertyElement.elementText("name").trim());
				valueNode = new ScalarNode(Tag.STR, propertyElement.elementText("value").trim());
				List<NodeTuple> propertyTuples = Lists.newArrayList(
						new NodeTuple(new ScalarNode(Tag.STR, "name"), nameNode), 
						new NodeTuple(new ScalarNode(Tag.STR, "value"), valueNode));
				propertyNodes.add(new MappingNode(Tag.MAP, propertyTuples, FlowStyle.BLOCK));
			}
		}
		if(!propertyNodes.isEmpty()) {
			keyNode = new ScalarNode(Tag.STR, "properties");
			tuples.add(new NodeTuple(keyNode, new SequenceNode(Tag.SEQ, propertyNodes, FlowStyle.BLOCK)));
		}
		
		MappingNode rootNode = new MappingNode(Tag.MAP, tuples, FlowStyle.BLOCK);
		StringWriter writer = new StringWriter();
		DumperOptions dumperOptions = new DumperOptions();
		Serializer serializer = new Serializer(new Emitter(writer, dumperOptions), 
				new Resolver(), dumperOptions, Tag.MAP);
		try {
			serializer.open();
			serializer.serialize(rootNode);
			serializer.close();
			return writer.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}