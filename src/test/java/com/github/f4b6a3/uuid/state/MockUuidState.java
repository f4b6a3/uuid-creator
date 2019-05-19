package com.github.f4b6a3.uuid.state;

public class MockUuidState extends AbstractUuidState {

	private boolean valid;
	
	@Override
	public void store() {
		// Do nothing
	}

	@Override
	public void load() {
		// Do nothing
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	@Override
	public boolean isValid() {
		return this.valid;
	}
}
