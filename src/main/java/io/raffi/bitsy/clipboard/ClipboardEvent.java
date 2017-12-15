package io.raffi.bitsy.clipboard;

public class ClipboardEvent {

	private String value;

	public ClipboardEvent ( String value ) {
		this.value = value;
	}

	public String getValue () {
		return this.value;
	}

}