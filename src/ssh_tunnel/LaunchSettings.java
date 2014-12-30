package ssh_tunnel;

import utils.Utils;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class LaunchSettings extends UiAutomatorTestCase {

	private static final String ID_TUNNEL_SWITCH = "android:id/checkbox";
	private static final String ID_CONNECTING = "android:id/message";
	private static final String TEXT_CONNECTING = "Connecting";
	private static final String ID_LISTVIEW = "android:id/list";
	private static final String TEXT_AUTO_CONNECT = "Auto Connect";

	/**
	 * Start proxy, restart it if already enabled
	 * @param button: checkbox to enable the proxy
	 * @return true if ok
	 * @throws UiObjectNotFoundException
	 */
	protected boolean startProxy(UiObject button)
			throws UiObjectNotFoundException {
		System.out.println("Start Proxy");
		// already started?
		if (button.isChecked()) {
			button.click(); // stop
			sleep(1000);
		}

		return Utils.clickAndWaitLoadingWindow(this, button, ID_CONNECTING,
				TEXT_CONNECTING, true);
	}

	protected void stopProxy(UiObject button) throws UiObjectNotFoundException {
		System.out.println("Stop Proxy");
		if (button.isChecked()) {
			button.click(); // stop
			sleep(1000);
		}
	}

	private void autoConnect(UiObject button, boolean enable)
			throws UiObjectNotFoundException {
		boolean running = button.isChecked();
		if (running) // cannot change options if enable...
			stopProxy(button);

		System.out.println("AutoConnect " + enable);
		UiObject checkBox = Utils.findCheckBoxInListWithTitle(ID_LISTVIEW,
				TEXT_AUTO_CONNECT);
		assertTrue("Unable to find element", checkBox != null);
		Utils.checkBox(checkBox, enable);

		if (running) { // restart proxy
			Utils.listMoveUp(ID_LISTVIEW);
			startProxy(button);
		}
	}

	public void testDemo() throws UiObjectNotFoundException {
		assertTrue("OOOOOpps",
				Utils.openApp(this, "SSHTunnel", "org.sshtunnel", false));
		sleep(1000);
		Utils.listMoveUp(ID_LISTVIEW);

		// Get button
		UiObject button = Utils.getObjectWithId(ID_TUNNEL_SWITCH);

		String action = getParams().getString("action");
		if (action == null) // default: start
			action = "start";

		if (action.equalsIgnoreCase("stop"))
			stopProxy(button);
		else if (action.equalsIgnoreCase("autoconnect"))
			autoConnect(true);
		else if (action.equalsIgnoreCase("notautoconnect"))
			autoConnect(false);
		else if (action.equalsIgnoreCase("stopnotautoconnect")) {
			stopProxy(button);
			autoConnect(false);
		} else if (action.equalsIgnoreCase("startautoconnect")) {
			startProxy(button);
			autoConnect(true);
		}
		else
			assertTrue("Not able to (re)start the proxy", startProxy(button));
	}
}
