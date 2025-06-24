public void onTaskSelectionEvent(@Observes TaskSelectionEvent event){
        selectedTaskId = event.getTaskId();
        selectedTaskName = event.getTaskName();
        
        view.getTaskIdAndName().setText(SafeHtmlUtils.htmlEscape(String.valueOf(selectedTaskId) + " - "+selectedTaskName));
        
        view.getContent().clear();
        
        String placeToGo;
        if(event.getPlace() != null && !event.getPlace().equals("")){
            placeToGo = event.getPlace();
        }else{
            placeToGo = "Task Details";
        }
        
        

        DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest(placeToGo);
        //Set Parameters here: 
        defaultPlaceRequest.addParameter("taskId", String.valueOf(selectedTaskId));
        defaultPlaceRequest.addParameter("taskName", selectedTaskName);

        Set<Activity> activities = activityManager.getActivities(defaultPlaceRequest);
        AbstractWorkbenchScreenActivity activity = ((AbstractWorkbenchScreenActivity) activities.iterator().next());
        
        activitiesMap.put(placeToGo, activity);
        
        IsWidget widget = activity.getWidget();
        activity.launch(place, null);
        activity.onStartup(defaultPlaceRequest);
        view.getContent().add(widget);
        activity.onOpen();
    }