package com.github.f4b6a3.uuid.state;

import com.github.f4b6a3.uuid.state.AbstractUuidState;

public class MockUuidState extends AbstractUuidState {

	private boolean valid;
	
	@Override
	public void store() {
	}

	@Override
	public void load() {
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	@Override
	public boolean isValid() {
		return this.valid;
	}
}
