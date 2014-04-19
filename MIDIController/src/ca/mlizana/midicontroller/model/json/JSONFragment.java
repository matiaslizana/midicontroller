package ca.mlizana.midicontroller.model.json;

import java.util.List;

public class JSONFragment {
	String id;
	List<JSONDeviceConfig> configList;
	public String getId() {
		return id;
	}
	public List<JSONDeviceConfig> getDeviceConfigurations() {
		return configList;
	}
}