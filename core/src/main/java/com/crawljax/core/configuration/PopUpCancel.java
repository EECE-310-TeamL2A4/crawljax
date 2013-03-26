package com.crawljax.core.configuration;

import com.crawljax.browser.EmbeddedBrowser.BrowserType;

/*Runs AutoHotKey compiled closePopUps.exe located in class path to cancel
 * specified pop ups
 */

public class PopUpCancel{

	private static BrowserType browserType = BrowserType.firefox;
	private static Process process = null;
	private final static String CLOSE_ALL = "ALL";
	private final static String CLOSE_NONE = "NONE";
	private final static String CLOSE_AUTHENTICATION = "AUTHENTICATION";
	private final static String CLOSE_DOWNLOAD = "DOWNLOAD";
	private static int timerPeriod = 500;
	private static String fileName = "closePopUps.exe";
	private static String mode = CLOSE_ALL;
	private static String exePath = PopUpCancel.class.getProtectionDomain().getCodeSource().
										getLocation().getPath() + fileName;
	
	//unique identifier for firefox dialog boxes
	private final static String fireFoxDialogID = "ahk_class MozillaDialogClass"; 
	private final static String ChromeDialogID = "ahk_class #32770";


	/** User selects what to cancel, unless file was not found
	 * @param newMode
	 */
	public static void setMode(String newMode) {
		switch(mode)
		{
		case(CLOSE_ALL):
		case(CLOSE_AUTHENTICATION):
		case(CLOSE_DOWNLOAD):
		case(CLOSE_NONE):
			mode = newMode; 
			break;	
		default: 
			System.err.println("Given invalid mode to PopUpCanceller. Mode of NONE selected");
			mode = CLOSE_NONE;
			break;
		} 
	}

	/** Set the timer delay to close pop ups
	 * @param delay
	 */
	public static void setTimer(int delay)
	{
		timerPeriod = delay;
	}

	/** Actual closing of the pop-ups performed here if mode set
	 */
	public static void ClosePopUps() {

		if(!mode.equals(CLOSE_NONE)) {

			try{
				String commands[] = new String[]{exePath, getPopUpTitle(), String.valueOf(timerPeriod) };
				process = Runtime.getRuntime().exec(commands);

			} catch (Exception ex) {
				System.err.println("Error closePopUps.exe not found. PopUpCancel disabled");
				mode = CLOSE_NONE;
			}
		}

	}

	/** Returns the distinct window title for the pop up window type specified by mode
	 * @return
	 */
	private static String getPopUpTitle() {
	
		String windowID = fireFoxDialogID;;
		String windowName =  "Opening";
		
		 
		switch(browserType){
		case chrome:
			windowID = ChromeDialogID;
			windowName = "Save As";
			break;
		case firefox:
			windowID = fireFoxDialogID;
			windowName = "Opening";
			break;
		}
		
		
		
		switch(mode)
		{
		case(CLOSE_ALL):
			break;
		case(CLOSE_AUTHENTICATION):
			windowID = "Authentication Required" + " " + windowID;
			break;
		case(CLOSE_DOWNLOAD):
			windowID = windowName + " " + windowID;
			break;		
		}
		return windowID;
	}


	public static void killExe(){

		try{
			if(process != null)
			{
				process.destroy();
			}
		}catch(Exception ex){
			System.err.println("closePopUps.exe was not closed properly");
		}

	}
	
	
	public static void setBrowserType(BrowserType ibrowserType)
	{
		browserType = ibrowserType;
	}
	
	public static String getFilePath()
	{
		return exePath;
	}

}