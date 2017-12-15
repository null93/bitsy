package io.raffi.bitsy.setting;

import io.raffi.bitsy.graphic.CheckItem;
import io.raffi.bitsy.Resource;

public class ClearOnQuit extends CheckItem {

	public ClearOnQuit ( Resource resource ) {
		//
		super ( "Clear clips on exit", "clearOnExit", resource );
	}

}