package ssh_tunnel;

import utils.Utils;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class LaunchSettings extends UiAutomatorTestCase {

	private static final String ID_TUNNEL_SWITCH = "android:id/checkbox";
	private static final String ID_CONNECTING = "android:id/message";

	protected void start_proxy() throws UiObjectNotFoundException {
		// Get button
		UiObject button = Utils.getObjectWithId(ID_TUNNEL_SWITCH);

		// already started?
		if (button.isChecked()) {
			button.click(); // stop
			sleep(1000);
		}
		button.click(); // just start
		sleep(500);

		// Wait for connection, max 10 sec
		for (int i = 0; i < 20; i++) {
			if (Utils.hasObject(ID_CONNECTING)
					&& Utils.hasText(ID_CONNECTING, "Connecting")) {
				System.out.println("Still connecting");
				sleep(500);
			}
			else
				return; // not object or another message, ok, we're connected.
		}
	}

	public void testDemo() throws UiObjectNotFoundException {
		assertTrue("OOOOOpps",
				Utils.openApp(this, "SSHTunnel", "org.sshtunnel"));
		sleep(1000);

		start_proxy();
		System.out.println("end");
	}
}

