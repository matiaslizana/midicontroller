package ca.mlizana.midicontroller.model;

import java.util.Arrays;
import java.util.List;

import ca.mlizana.midicontroller.MainActivity;
import ca.mlizana.midicontroller.MainActivity.LOGTAG;
import ca.mlizana.midicontroller.exceptions.ComunicationException;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class MIDIDevice {

	private String id;
	private String description;
	private UsbDevice device;
	private UsbInterface intf;
	private UsbEndpoint epIn;
	private UsbEndpoint epOut;

	public MIDIDevice(String id, String description, UsbDevice device) {
		this.id = id;
		this.description = description;
		this.device = device;
	}

	public String getId() {
		return id;
	}

	public boolean setComunication(int f) throws ComunicationException {
		if (device != null) {
			intf = device.getInterface(f);
			if (intf.getEndpointCount() == 2) {
				UsbEndpoint ep1 = intf.getEndpoint(0);
				UsbEndpoint ep2 = intf.getEndpoint(1);
				if (ep1.getDirection() == UsbConstants.USB_DIR_IN) {
					epIn = ep1;
				} else {
					epOut = ep1;
				}
				if (ep2.getDirection() == UsbConstants.USB_DIR_IN) {
					epIn = ep2;
				} else {
					epOut = ep2;
				}
				if (intf == null || epIn == null || epOut == null) {
					throw new ComunicationException(
							"Cannot set MIDIDevice interface, or either endpoints");
				}
			} else {
				throw new ComunicationException("Interface has not 2 endpoints");
			}
		} else {
			throw new ComunicationException(
					"The device is null and no comunication can be established");
		}
		return true;
	}

	// 0x15 is the first note A0
	public void sendMidiMessage(UsbManager manager, String message) {

		try {

			byte[] writeBuffer = new byte[epOut.getMaxPacketSize()];

			writeBuffer[0] = (byte) (((0 & 0xf) << 4) | (0x9 & 0xf));
			writeBuffer[1] = (byte) (Integer.valueOf(message.substring(0, 2),16) | (0 & 0xf));
			int count = 2;
			for (int i = 2; i < message.length(); i += 2) {
				writeBuffer[count] = (byte) (Integer.valueOf(message.substring(i,i+2),16) | 0x00);
				count++;
			}
			MainActivity.log("MIDI Sent: " + message, LOGTAG.INFO);

			// Open a connection
			UsbDeviceConnection connection = manager.openDevice(device);
			connection.claimInterface(intf, true);
			connection.bulkTransfer(epOut, writeBuffer, writeBuffer.length, 0);
			connection.close();

		} catch (Exception e) {
			MainActivity.log("Cannot send MIDI message!", LOGTAG.ERROR);
		}

	}

}
