package ssh_tunnel;

import utils.Utils;

import com.android.uiautomator.core.UiCollection;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class LaunchSettings extends UiAutomatorTestCase {

	private static final String ID_TUNNEL_SWITCH = "android:id/checkbox";
	private static final String ID_CONNECTING = "android:id/message";
	private static final String ID_LISTVIEW = "android:id/list";
	private static final String ID_LIST_ELEM_TITLE = "android:id/title";
	private static final String CLASS_LINEAR_LAYOUT = android.widget.LinearLayout.class
			.getName();
	private static final String TEXT_AUTO_CONNECT = "Auto Connect";

	/**
	 * Start proxy, restart it if already enabled
	 * @param button: checkbox to enable the proxy
	 * @return true if ok
	 * @throws UiObjectNotFoundException
	 */
	protected boolean startProxy(UiObject button)
			throws UiObjectNotFoundException {
		// already started?
		if (button.isChecked()) {
			button.click(); // stop
			sleep(1000);
		}
		button.click(); // just start
		sleep(500);

		// Wait for connection, max 20 sec
		for (int i = 0; i < 40; i++) {
			if (Utils.hasObject(ID_CONNECTING)
					&& Utils.hasText(ID_CONNECTING, "Connecting")) {
				System.out.println("Still connecting");
				sleep(500);
			}
			else // no object or another message, ok, we're connected if checked
				return button.isChecked();
		}
		return false;
	}

	protected void stopProxy(UiObject button) throws UiObjectNotFoundException {
		if (button.isChecked()) {
			button.click(); // stop
			sleep(1000);
		}
	}

	private void autoConnect(boolean enable) throws UiObjectNotFoundException {
		// Scroll the view until finding our file
		boolean found = false;
		UiScrollable list = Utils.getScrollableWithId(ID_LISTVIEW);
		list.setAsVerticalList();

		while (!found) {
			UiCollection listView = new UiCollection(
					new UiSelector().resourceId(ID_LISTVIEW));
			int count = listView.getChildCount(new UiSelector()
					.className(CLASS_LINEAR_LAYOUT));
			for (int i = 0; i < count; i++) {
				UiObject linearLayout = listView.getChild(new UiSelector()
						.className(CLASS_LINEAR_LAYOUT).instance(i));
				UiObject title = linearLayout.getChild(new UiSelector()
						.resourceId(ID_LIST_ELEM_TITLE));
				if (title.exists() && title.getText().equals(TEXT_AUTO_CONNECT)) {
					UiObject checkBox = linearLayout.getChild(new UiSelector()
							.resourceId(ID_TUNNEL_SWITCH));
					if ((enable && !checkBox.isChecked())
							|| (!enable && checkBox.isChecked()))
						assertTrue("Unable to select element",
								Utils.click(checkBox));
					found = true;
					break;
				}
			}

			if (!found) {
				assertTrue("Didn't find the requested object...",
						Utils.scrollForward(list));
			}
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
