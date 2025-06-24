public void setPreservedState(String stateKey, Object preservedState) {
		state.clear();
		state.put(stateKey, preservedState);
	}