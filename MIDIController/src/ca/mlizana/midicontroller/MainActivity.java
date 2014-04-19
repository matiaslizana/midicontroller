package ca.mlizana.midicontroller;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gson.Gson;

import ca.mlizana.midicontroller.exceptions.ComunicationException;
import ca.mlizana.midicontroller.model.MIDIController;
import ca.mlizana.midicontroller.model.MIDIDevice;
import ca.mlizana.midicontroller.model.json.JSONConfig;
import ca.mlizana.midicontroller.model.json.JSONDevice;
import ca.mlizana.midicontroller.model.json.JSONDeviceConfig;
import ca.mlizana.midicontroller.model.json.JSONFragment;
import ca.mlizana.midicontroller.model.json.JSONSong;
import ca.mlizana.midicontroller.views.DevicesListAdapter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

public class MainActivity extends Activity {

	private JSONFragment currentFragment;
	private JSONSong currentSong;
	private int fragmentIndex;
	private int songIndex;
	private ListView devicesListView;
	private UsbManager mUsbManager;
	private HashMap<String,MIDIDevice> devices = new HashMap<String, MIDIDevice>();
	//Check if the pedal controller has 2 endpoints in the interface, and check if it's the same as MIDIDevice
	private MIDIController controller;
	private JSONConfig config;
    private static TextView log;
	public static enum LOGTAG { INFO, WARN, ERROR }
    
    public static void log(String message, LOGTAG tag) {
    	switch(tag){
    		case WARN:
    			updateLog(message,Color.YELLOW);
    			break;
    		case ERROR:
    			updateLog(message,Color.RED);
    			break;
    		default:
    			updateLog(message,Color.GRAY);
    			break;
    	}
    }
    
    private static void updateLog(String message,int color) {
    	if(log!=null){
	    	log.setTextColor(color);
			log.setText(message+"\n"+log.getText());
    	}
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Views
		setContentView(R.layout.activity_main);

		//Takes log control
		log = (TextView) findViewById(R.id.textLog);
		
		//Reading configuration from JSON
		readConfiguration();
		
		//Connections
		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		connectDevices();
		
		//Configure Devices ListView
		devicesListView = (ListView) findViewById(R.id.devicesLayout);
		devicesListView.setAdapter(new DevicesListAdapter(this,getDevicesListView()));
		
		//Initialize First Configuration
		initializeSetlist();
		update();	
		
	}

	private ArrayList<String> getDevicesListView() {
		ArrayList<String> d = new ArrayList<String>();
		for(MIDIDevice dev : devices.values()) {
			d.add(dev.getId());
		}
		log("Devices list: "+d.toString(), LOGTAG.INFO);
		return d;
	}
	
	private void update() {
		//Update the view
		updateView();
		//Update devices config
		updateConfig();		
	}

	private void updateConfig() {
		for (JSONDeviceConfig dc : currentFragment.getDeviceConfigurations()) {
			MIDIDevice device = devices.get(dc.getDevice());
			if(device!=null){
				for (String m : dc.getMessages()) {
					device.sendMidiMessage(mUsbManager,m);
				}
			}
		}
	}
	
	private void updateView() {
		TextView song = (TextView)findViewById(R.id.textSong);
		TextView fragment = (TextView)findViewById(R.id.textFragment);
		song.setText(currentSong.getTitle());
		fragment.setText(currentFragment.getId());
	}
	
	private void initializeSetlist() {
		songIndex = 0;
		currentSong = config.getSetlist().get(songIndex);
		fragmentIndex = 0;
		currentFragment = currentSong.getFragments().get(fragmentIndex);
	}
	
	public void nextFragmentView(View view) {
		nextFragment();
	}
	
	private void nextFragment() {
		if(fragmentIndex == currentSong.getFragments().size() - 1) {
			if(songIndex == config.getSetlist().size() - 1) {
				songIndex = 0;
				fragmentIndex = 0;
			}else{
				songIndex++;
				currentSong = config.getSetlist().get(songIndex);
			}
			fragmentIndex = 0;
		}else{
			fragmentIndex++;
		}		
		currentFragment = currentSong.getFragments().get(fragmentIndex);
		update();
	}	

	private void previousFragment() {
		if(fragmentIndex == 0) {
			if(songIndex == 0) {
				songIndex = config.getSetlist().size()-1;
				fragmentIndex = currentSong.getFragments().size()-1;
			}else{
				songIndex--;
			}
			fragmentIndex = currentSong.getFragments().size()-1;
		}else{
			fragmentIndex++;
		}
		currentSong = config.getSetlist().get(songIndex);
		currentFragment = currentSong.getFragments().get(fragmentIndex);
		updateView();
		updateConfig();
	}

	private void connectDevices() {
		HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
		Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
		UsbDevice d = null;
		while (deviceIterator.hasNext()) {
			d = deviceIterator.next();
			log("New device found: (id:"+d.getDeviceId()+",vendor:"+d.getVendorId()+",product:"+d.getProductId()+",class:"+d.getDeviceClass()+",subclass:"+d.getDeviceSubclass()+",protocol:"+d.getDeviceProtocol()+")",LOGTAG.INFO);
			//Searching for MIDI Devices
			for (JSONDevice jd : config.getDevices()) {
				if(d.getVendorId()==jd.getVendorId() &&
				   d.getProductId()==jd.getProductId() &&
				   d.getDeviceClass()==jd.getClaz() && 
				   d.getDeviceSubclass()==jd.getSubclass() &&
				   d.getDeviceProtocol()==jd.getProtocol()) {
					MIDIDevice dev = new MIDIDevice(jd.getId(), jd.getDescription(), d);
					log("New MIDIDevice obtained: "+dev.getId(),LOGTAG.INFO);
					try {
						dev.setComunication(jd.getInterface());
					}catch(ComunicationException e) {
						log(e.getMessage(), LOGTAG.ERROR);
					}
					devices.put(jd.getId(), dev);
					log("Device added: " + jd.getId() + " ("+dev+")", LOGTAG.INFO);
				}
			}
			//Searching for MIDI Controller
			JSONDevice ct = config.getController();
			if(d.getVendorId()==ct.getVendorId() &&
					   d.getProductId()==ct.getProductId() &&
					   d.getDeviceClass()==ct.getClaz() && 
					   d.getDeviceSubclass()==ct.getSubclass() &&
					   d.getDeviceProtocol()==ct.getProtocol()) {
				MIDIController c = new MIDIController();
				controller = c;
			}
		}
	}
	
	private void readConfiguration() {
		Gson c = new Gson();
		try {		
			InputStream rawResource = getApplicationContext().getResources().openRawResource(R.raw.conf);
			Reader fr = new InputStreamReader(rawResource);
			config = c.fromJson(fr, JSONConfig.class);
			fr.close();
		} catch (Exception e) {
			Log.e("", "Cannot load the JSON configuration file");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
}
