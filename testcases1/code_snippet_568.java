public SerializedInfoflowResults readResults(String fileName) throws XMLStreamException, IOException {
		SerializedInfoflowResults results = new SerializedInfoflowResults();
		InfoflowPerformanceData perfData = null;

		XMLStreamReader reader = null;
		try (InputStream in = new FileInputStream(fileName)) {
			XMLInputFactory factory = XMLInputFactory.newInstance();
			factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
			factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
			reader = factory.createXMLStreamReader(in);

			String statement = null;
			String method = null;
			String apValue = null;
			String apValueType = null;
			boolean apTaintSubFields = false;
			List<String> apFields = new ArrayList<>();
			List<String> apTypes = new ArrayList<>();
			SerializedAccessPath ap = null;
			SerializedSinkInfo sink = null;
			SerializedSourceInfo source = null;
			List<SerializedPathElement> pathElements = new ArrayList<>();

			Stack<State> stateStack = new Stack<>();
			stateStack.push(State.init);

			while (reader.hasNext()) {
				// Read the next tag
				reader.next();
				if (!reader.hasName())
					continue;

				if (reader.getLocalName().equals(XmlConstants.Tags.root) && reader.isStartElement()
						&& stateStack.peek() == State.init) {
					stateStack.push(State.dataFlowResults);

					// Load the attributes of the root node
					results.setFileFormatVersion(
							int2Str(getAttributeByName(reader, XmlConstants.Attributes.fileFormatVersion)));
				} else if (reader.getLocalName().equals(XmlConstants.Tags.results) && reader.isStartElement()
						&& stateStack.peek() == State.dataFlowResults) {
					stateStack.push(State.results);
				} else if (reader.getLocalName().equals(XmlConstants.Tags.result) && reader.isStartElement()
						&& stateStack.peek() == State.results) {
					stateStack.push(State.result);
				} else if (reader.getLocalName().equals(XmlConstants.Tags.sink) && reader.isStartElement()
						&& stateStack.peek() == State.result) {
					stateStack.push(State.sink);

					// Read the attributes
					statement = getAttributeByName(reader, XmlConstants.Attributes.statement);
				} else if (reader.getLocalName().equals(XmlConstants.Tags.accessPath) && reader.isStartElement()) {
					stateStack.push(State.accessPath);

					// Read the attributes
					apValue = getAttributeByName(reader, XmlConstants.Attributes.value);
					apValueType = getAttributeByName(reader, XmlConstants.Attributes.type);
					apTaintSubFields = getAttributeByName(reader, XmlConstants.Attributes.taintSubFields)
							.equals(XmlConstants.Values.TRUE);

					// Clear the fields
					apFields.clear();
					apTypes.clear();
				} else if (reader.getLocalName().equals(XmlConstants.Tags.fields) && reader.isStartElement()
						&& stateStack.peek() == State.accessPath) {
					stateStack.push(State.fields);
				} else if (reader.getLocalName().equals(XmlConstants.Tags.field) && reader.isStartElement()
						&& stateStack.peek() == State.fields) {
					stateStack.push(State.field);

					// Read the attributes
					String value = getAttributeByName(reader, XmlConstants.Attributes.value);
					String type = getAttributeByName(reader, XmlConstants.Attributes.type);
					if (value != null && !value.isEmpty() && type != null && !type.isEmpty()) {
						apFields.add(value);
						apTypes.add(value);
					}
				} else if (reader.getLocalName().equals(XmlConstants.Tags.sources) && reader.isStartElement()
						&& stateStack.peek() == State.result) {
					stateStack.push(State.sources);
				} else if (reader.getLocalName().equals(XmlConstants.Tags.source) && reader.isStartElement()
						&& stateStack.peek() == State.sources) {
					stateStack.push(State.source);

					// Read the attributes
					statement = getAttributeByName(reader, XmlConstants.Attributes.statement);
					method = getAttributeByName(reader, XmlConstants.Attributes.method);
				} else if (reader.getLocalName().equals(XmlConstants.Tags.taintPath) && reader.isStartElement()
						&& stateStack.peek() == State.source) {
					stateStack.push(State.taintPath);

					// Clear the old state
					pathElements.clear();
				} else if (reader.getLocalName().equals(XmlConstants.Tags.pathElement) && reader.isStartElement()
						&& stateStack.peek() == State.source) {
					stateStack.push(State.taintPath);

					// Read the attributes
					statement = getAttributeByName(reader, XmlConstants.Attributes.statement);
					method = getAttributeByName(reader, XmlConstants.Attributes.method);
				} else if (reader.getLocalName().equals(XmlConstants.Tags.performanceData) && reader.isStartElement()
						&& stateStack.peek() == State.dataFlowResults) {
					stateStack.push(State.performanceData);
				} else if (reader.getLocalName().equals(XmlConstants.Tags.performanceEntry) && reader.isStartElement()
						&& stateStack.peek() == State.performanceData) {
					stateStack.push(State.performanceEntry);

					// We need a performance data object
					if (perfData == null)
						perfData = results.getOrCreatePerformanceData();

					// Read the attributes
					String perfName = getAttributeByName(reader, XmlConstants.Attributes.name);
					String perfValue = getAttributeByName(reader, XmlConstants.Attributes.value);

					switch (perfName) {
					case XmlConstants.Values.PERF_CALLGRAPH_SECONDS:
						perfData.setCallgraphConstructionSeconds(Integer.parseInt(perfValue));
						break;
					case XmlConstants.Values.PERF_PATH_RECONSTRUCTION_SECONDS:
						perfData.setPathReconstructionSeconds(Integer.parseInt(perfValue));
						break;
					case XmlConstants.Values.PERF_TAINT_PROPAGATION_SECONDS:
						perfData.setTaintPropagationSeconds(Integer.parseInt(perfValue));
						break;
					case XmlConstants.Values.PERF_TOTAL_RUNTIME_SECONDS:
						perfData.setTotalRuntimeSeconds(Integer.parseInt(perfValue));
						break;
					case XmlConstants.Values.PERF_MAX_MEMORY_CONSUMPTION:
						perfData.setMaxMemoryConsumption(Integer.parseInt(perfValue));
						break;
					case XmlConstants.Values.PERF_SOURCE_COUNT:
						perfData.setSourceCount(Integer.parseInt(perfValue));
						break;
					case XmlConstants.Values.PERF_SINK_COUNT:
						perfData.setSinkCount(Integer.parseInt(perfValue));
						break;
					}
				} else if (reader.isEndElement()) {
					stateStack.pop();

					if (reader.getLocalName().equals(XmlConstants.Tags.accessPath))
						ap = new SerializedAccessPath(apValue, apValueType, apTaintSubFields,
								apFields.toArray(new String[apFields.size()]),
								apTypes.toArray(new String[apTypes.size()]));
					else if (reader.getLocalName().equals(XmlConstants.Tags.sink))
						sink = new SerializedSinkInfo(ap, statement, method);
					else if (reader.getLocalName().equals(XmlConstants.Tags.source))
						source = new SerializedSourceInfo(ap, statement, method, pathElements);
					else if (reader.getLocalName().equals(XmlConstants.Tags.result))
						results.addResult(source, sink);
					else if (reader.getLocalName().equals(XmlConstants.Tags.pathElement))
						pathElements.add(new SerializedPathElement(ap, statement, method));
				}
			}

			return results;
		} finally {
			if (reader != null)
				reader.close();
		}
	}