package ca.mlizana.midicontroller.model.json;

public class JSONDevice {
	String id;
	String description;
	int intf;
	int vendorId;
	int productId;
	int claz;
	int subclass;
	int protocol;
	
	public String getId() {
		return id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getInterface() {
		return intf;
	}
	
	public int getVendorId() {
		return vendorId;
	}
	
	public int getProductId() {
		return productId;
	}
	
	public int getClaz() {
		return claz;
	}
	
	public int getSubclass() {
		return subclass;
	}
	
	public int getProtocol() {
		return protocol;
	}
}