package ca.mlizana.midicontroller.model.json;

import java.util.List;

public class JSONConfig {

	JSONDevice controller;
	List<JSONDevice> devices;
	List<JSONSong> setlist;

	public JSONDevice getController() {
		return controller;
	}
	
	public List<JSONDevice> getDevices() {
		return devices;
	}
	
	public List<JSONSong> getSetlist() {
		return setlist;
	}
	
}