package ca.mlizana.midicontroller.model.json;

import java.util.List;

public class JSONSong {
	String title;
	List<JSONFragment> fragments;
	public String getTitle() {
		return title;
	}
	public List<JSONFragment> getFragments () {
		return fragments;
	}
}