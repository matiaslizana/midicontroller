package ca.mlizana.midicontroller.model.json;

import java.util.List;

public class JSONDeviceConfig {
	String device;
	List<String> messages;
	
	public String getDevice() {
		return device;
	}
	
	public List<String> getMessages() {
		return messages;
	}
}