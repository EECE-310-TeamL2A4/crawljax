package com.crawljax.core.configuration;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import com.crawljax.browser.EmbeddedBrowser.BrowserType;

/*
 * Runs AutoHotKey compiled closePopUps.exe located in class path to cancel specified pop ups
 */

public class PopUpCancel {

	// unique identifier for firefox dialog boxes
	private final static String fireFoxDialogID = "ahk_class MozillaDialogClass";
	private final static String ChromeDialogID = "ahk_class #32770";
<<<<<<< HEAD

	private final static String fireFoxWindowName = "Save As";
	private final static String ChromeWindowName = "Opening";
=======
>>>>>>> 23c0d92bf4253efaa1e967134b92b648a55f135a

	private final static String CLOSE_ALL = "ALL";
	private final static String CLOSE_NONE = "NONE";
	private final static String CLOSE_AUTHENTICATION = "AUTHENTICATION";
	private final static String CLOSE_DOWNLOAD = "DOWNLOAD";

	private static BrowserType browserType = BrowserType.firefox;
	private static Process process = null;
	private static File temporaryExe = null;
	private static int killProcessTimeOut = 500;
	private static int timerPeriod = 500;
	private static String fileName = "/closePopUps.exe";
	private static String mode = CLOSE_ALL;
	private static String exePath = null;
<<<<<<< HEAD

	/**
	 * Create temporary file to store exe file into using streams
	 * 
	 * @return string path of temporary file
	 */
	private static String createTempExe()
	{
		String path = null;
		InputStream input = null;
		OutputStream output = null;

		try {
			input = PopUpCancel.class.getResourceAsStream(fileName);
			temporaryExe = File.createTempFile(PopUpCancel.class.getName(), "");
			output = new FileOutputStream(temporaryExe);
			output = new BufferedOutputStream(output);
			IOUtils.copy(input, output);

		} catch (Exception ex) {
		} finally { // in case copy fails
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
			path = temporaryExe.getAbsolutePath();
		}

		return path;
	}

=======

	// PopUpCancel.class.getProtectionDomain().getCodeSource().getLocation().getPath() + fileName;

	/**
	 * Create temporary file to store exe file into using streams
	 * 
	 * @return string path of temporary file
	 */
	private static String getPopUpCancelExe()
	{
		String path = null;
		InputStream input = null;
		OutputStream output = null;

		try {
			input = PopUpCancel.class.getResourceAsStream(fileName);
			temporaryExe = File.createTempFile(PopUpCancel.class.getName(), "");
			output = new FileOutputStream(temporaryExe);
			output = new BufferedOutputStream(output);
			IOUtils.copy(input, output);

		} catch (Exception ex) {
		} finally { // in case copy fails
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
			path = temporaryExe.getAbsolutePath();
		}

		return path;
	}

>>>>>>> 23c0d92bf4253efaa1e967134b92b648a55f135a
	/**
	 * User selects what to cancel, unless file was not found
	 * 
	 * @param newMode
	 */
	public static void setMode(String newMode) {

		switch (mode)
		{
			case (CLOSE_ALL):
			case (CLOSE_AUTHENTICATION):
			case (CLOSE_DOWNLOAD):
			case (CLOSE_NONE):
				mode = newMode;
				break;
			default:
				System.err.println("Given invalid mode to PopUpCanceller. Mode of NONE selected");
				mode = CLOSE_NONE;
				break;
		}
	}

	/**
	 * Set the timer delay to close pop ups
	 * 
	 * @param delay
	 */
	public static void setTimer(int delay) {
		timerPeriod = delay;
	}

	/**
	 * Run closePopUps.exe to close pop ups
	 */
	public static void closePopUps() {

		if (!mode.equals(CLOSE_NONE)) {

<<<<<<< HEAD
			try {

				if (exePath == null)
					exePath = createTempExe();
=======
		if (!mode.equals(CLOSE_NONE)) {

			try {

				if (exePath == null)
					exePath = getPopUpCancelExe();
>>>>>>> 23c0d92bf4253efaa1e967134b92b648a55f135a

				// The window class ID and title name is passed as parameters to the exe
				String commands[] =
				        new String[] { exePath, getPopUpTitle(), String.valueOf(timerPeriod) };
				process = Runtime.getRuntime().exec(commands);

			} catch (Exception ex) {
				System.err.println("Error" + fileName + " not found. PopUpCancel disabled");
				mode = CLOSE_NONE;
			}
		}

	}

	public static void setBrowserType(BrowserType ibrowserType) {
		browserType = ibrowserType;
	}

	/**
	 * Returns the distinct window title for the pop up window type specified by mode
	 * 
	 * @return
	 */
	private static String getPopUpTitle() {

		String windowID = fireFoxDialogID;
<<<<<<< HEAD
		String windowName = fireFoxWindowName;

		// set Window Class ID for browser type
		switch (browserType) {
			case chrome:
				windowID = ChromeDialogID;
				windowName = ChromeWindowName;
				break;
			case firefox:
				windowID = fireFoxDialogID;
				windowName = fireFoxWindowName;
				break;
		}

		// Set arguments for exe to close based on mode selected
=======
		;
		String windowName = "Opening";

		switch (browserType) {
			case chrome:
				windowID = ChromeDialogID;
				windowName = "Save As";
				break;
			case firefox:
				windowID = fireFoxDialogID;
				windowName = "Opening";
				break;
		}

>>>>>>> 23c0d92bf4253efaa1e967134b92b648a55f135a
		switch (mode)
		{
			case (CLOSE_ALL):
				break;
			case (CLOSE_AUTHENTICATION):
				windowID = "Authentication Required" + " " + windowID;
				break;
			case (CLOSE_DOWNLOAD):
				windowID = windowName + " " + windowID;
				break;
		}
		return windowID;
<<<<<<< HEAD
	}

	/**
	 * How long we should wait before we try to kill the exe (default 500 ms)
	 * 
	 * @param timerVal
	 */
	public static void setKillProcessTimeOut(int timerVal)
	{
		killProcessTimeOut = timerVal;
	}

	/**
=======
	}

	/**
	 * How long we should wait before we try to kill the exe (default 500 ms)
	 * 
	 * @param timerVal
	 */
	public static void setKillProcessTimeOut(int timerVal)
	{
		killProcessTimeOut = timerVal;
	}

	/**
>>>>>>> 23c0d92bf4253efaa1e967134b92b648a55f135a
	 * Get path of exe
	 * 
	 * @return
	 */
	public static String getFilePath()
	{
		return exePath;
	}

	public static void killExe() {

		try {
			if (process != null)
			{
<<<<<<< HEAD
				Thread KillProcessThread = (new Thread(new ProcessCleanUp(process)));
				KillProcessThread.start();
				KillProcessThread.join(killProcessTimeOut);
=======
				process.destroy();
				Thread thread = (new Thread(new ProcessCleanUp(process)));
				thread.start();
				thread.join(killProcessTimeOut);
>>>>>>> 23c0d92bf4253efaa1e967134b92b648a55f135a

				if (!temporaryExe.delete()) {
					System.err
					        .println("PopUpCanceler clean up failed (temp file was not deleted");
				}
			}
			exePath = null;
		} catch (NullPointerException ex) {
			// Means user didn't want to cancel pop ups
		} catch (InterruptedException ex) {
			System.err.println("deleting process was interrupted. " +
			        "File may not have been deleted properly or ended");
		}
	}

	private static class ProcessCleanUp implements Runnable
	{
		Process process;
<<<<<<< HEAD
=======

		public ProcessCleanUp(Process process)
		{
			this.process = process;
		}

		@Override
		public void run() {
			try {
				this.process.waitFor();
			} catch (InterruptedException e) {
				System.err.println(fileName + " did not gracefully exit");
			}
		}

	}

	public static String getMode() {
		return mode;
	}
>>>>>>> 23c0d92bf4253efaa1e967134b92b648a55f135a

		public ProcessCleanUp(Process process)
		{
			this.process = process;
			process.destroy();
		}

		@Override
		public void run() {
			try {
				this.process.waitFor();
			} catch (InterruptedException e) {
				System.err.println(fileName + " did not gracefully exit");
			}
		}

	}
}