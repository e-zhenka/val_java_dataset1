private RootBeanDefinition registerMessageBroker(Element brokerElement,
			RuntimeBeanReference inChannel, RuntimeBeanReference outChannel, RuntimeBeanReference brokerChannel,
			Object userDestHandler, RuntimeBeanReference brokerTemplate,
			RuntimeBeanReference userRegistry, ParserContext context, Object source) {

		Element simpleBrokerElem = DomUtils.getChildElementByTagName(brokerElement, "simple-broker");
		Element brokerRelayElem = DomUtils.getChildElementByTagName(brokerElement, "stomp-broker-relay");

		ConstructorArgumentValues cavs = new ConstructorArgumentValues();
		cavs.addIndexedArgumentValue(0, inChannel);
		cavs.addIndexedArgumentValue(1, outChannel);
		cavs.addIndexedArgumentValue(2, brokerChannel);

		RootBeanDefinition brokerDef;
		if (simpleBrokerElem != null) {
			String prefix = simpleBrokerElem.getAttribute("prefix");
			cavs.addIndexedArgumentValue(3, Arrays.asList(StringUtils.tokenizeToStringArray(prefix, ",")));
			brokerDef = new RootBeanDefinition(SimpleBrokerMessageHandler.class, cavs, null);
			if (brokerElement.hasAttribute("path-matcher")) {
				String pathMatcherRef = brokerElement.getAttribute("path-matcher");
				brokerDef.getPropertyValues().add("pathMatcher", new RuntimeBeanReference(pathMatcherRef));
			}
			if (simpleBrokerElem.hasAttribute("scheduler")) {
				String scheduler = simpleBrokerElem.getAttribute("scheduler");
				brokerDef.getPropertyValues().add("taskScheduler", new RuntimeBeanReference(scheduler));
			}
			if (simpleBrokerElem.hasAttribute("heartbeat")) {
				String heartbeatValue = simpleBrokerElem.getAttribute("heartbeat");
				brokerDef.getPropertyValues().add("heartbeatValue", heartbeatValue);
			}
		}
		else if (brokerRelayElem != null) {
			String prefix = brokerRelayElem.getAttribute("prefix");
			cavs.addIndexedArgumentValue(3, Arrays.asList(StringUtils.tokenizeToStringArray(prefix, ",")));

			MutablePropertyValues values = new MutablePropertyValues();
			if (brokerRelayElem.hasAttribute("relay-host")) {
				values.add("relayHost", brokerRelayElem.getAttribute("relay-host"));
			}
			if (brokerRelayElem.hasAttribute("relay-port")) {
				values.add("relayPort", brokerRelayElem.getAttribute("relay-port"));
			}
			if (brokerRelayElem.hasAttribute("client-login")) {
				values.add("clientLogin", brokerRelayElem.getAttribute("client-login"));
			}
			if (brokerRelayElem.hasAttribute("client-passcode")) {
				values.add("clientPasscode", brokerRelayElem.getAttribute("client-passcode"));
			}
			if (brokerRelayElem.hasAttribute("system-login")) {
				values.add("systemLogin", brokerRelayElem.getAttribute("system-login"));
			}
			if (brokerRelayElem.hasAttribute("system-passcode")) {
				values.add("systemPasscode", brokerRelayElem.getAttribute("system-passcode"));
			}
			if (brokerRelayElem.hasAttribute("heartbeat-send-interval")) {
				values.add("systemHeartbeatSendInterval", brokerRelayElem.getAttribute("heartbeat-send-interval"));
			}
			if (brokerRelayElem.hasAttribute("heartbeat-receive-interval")) {
				values.add("systemHeartbeatReceiveInterval", brokerRelayElem.getAttribute("heartbeat-receive-interval"));
			}
			if (brokerRelayElem.hasAttribute("virtual-host")) {
				values.add("virtualHost", brokerRelayElem.getAttribute("virtual-host"));
			}
			ManagedMap<String, Object> map = new ManagedMap<String, Object>();
			map.setSource(source);
			if (brokerRelayElem.hasAttribute("user-destination-broadcast")) {
				String destination = brokerRelayElem.getAttribute("user-destination-broadcast");
				map.put(destination, userDestHandler);
			}
			if (brokerRelayElem.hasAttribute("user-registry-broadcast")) {
				String destination = brokerRelayElem.getAttribute("user-registry-broadcast");
				map.put(destination, registerUserRegistryMessageHandler(userRegistry,
						brokerTemplate, destination, context, source));
			}
			if (!map.isEmpty()) {
				values.add("systemSubscriptions", map);
			}
			Class<?> handlerType = StompBrokerRelayMessageHandler.class;
			brokerDef = new RootBeanDefinition(handlerType, cavs, values);
		}
		else {
			// Should not happen
			throw new IllegalStateException("Neither <simple-broker> nor <stomp-broker-relay> elements found.");
		}
		registerBeanDef(brokerDef, context, source);
		return brokerDef;
	}