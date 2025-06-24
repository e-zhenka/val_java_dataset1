private Changes handleRequest(ContextRequest contextRequest, Session session, Profile profile, ContextResponse data,
                                  ServletRequest request, ServletResponse response, Date timestamp) {
        Changes changes = ServletCommon.handleEvents(contextRequest.getEvents(), session, profile, request, response, timestamp,
                privacyService, eventService);
        data.setProcessedEvents(changes.getProcessedItems());

        profile = changes.getProfile();

        if (contextRequest.isRequireSegments()) {
            data.setProfileSegments(profile.getSegments());
        }

        if (contextRequest.getRequiredProfileProperties() != null) {
            Map<String, Object> profileProperties = new HashMap<>(profile.getProperties());
            if (!contextRequest.getRequiredProfileProperties().contains("*")) {
                profileProperties.keySet().retainAll(contextRequest.getRequiredProfileProperties());
            }
            data.setProfileProperties(profileProperties);
        }

        if (session != null) {
            data.setSessionId(session.getItemId());
            if (contextRequest.getRequiredSessionProperties() != null) {
                Map<String, Object> sessionProperties = new HashMap<>(session.getProperties());
                if (!contextRequest.getRequiredSessionProperties().contains("*")) {
                    sessionProperties.keySet().retainAll(contextRequest.getRequiredSessionProperties());
                }
                data.setSessionProperties(sessionProperties);
            }
        }

        processOverrides(contextRequest, profile, session);

        List<PersonalizationService.PersonalizedContent> filterNodes = contextRequest.getFilters();
        if (filterNodes != null) {
            data.setFilteringResults(new HashMap<>());
            for (PersonalizationService.PersonalizedContent personalizedContent : filterNodes) {
                data.getFilteringResults().put(personalizedContent.getId(), personalizationService.filter(profile,
                        session, personalizedContent));
            }
        }

        List<PersonalizationService.PersonalizationRequest> personalizations = contextRequest.getPersonalizations();
        if (personalizations != null) {
            data.setPersonalizations(new HashMap<>());
            for (PersonalizationService.PersonalizationRequest personalization : personalizations) {
                data.getPersonalizations().put(personalization.getId(), personalizationService.personalizeList(profile,
                        session, personalization));
            }
        }

        if (!(profile instanceof Persona)) {
            data.setTrackedConditions(rulesService.getTrackedConditions(contextRequest.getSource()));
        } else {
            data.setTrackedConditions(Collections.emptySet());
        }

        data.setAnonymousBrowsing(privacyService.isRequireAnonymousBrowsing(profile));
        data.setConsents(profile.getConsents());

        return changes;
    }