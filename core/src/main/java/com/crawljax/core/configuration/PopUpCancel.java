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
	private static String fileNameFirefox = "closePopUps.exe";
	private static String fileNameChrome = "closePopUpsChrome.exe";
	private static String mode = CLOSE_ALL;
	private static String exePath = PopUpCancel.class.getProtectionDomain().getCodeSource().
										getLocation().getPath() + fileNameFirefox;
	
	//unique identifier for firefox dialog boxes
	private final static String fireFoxDialogID = "ahk_class MozillaDialogClass"; 


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
	
		String type = fireFoxDialogID;
		
		switch(mode)
		{
		case(CLOSE_ALL):
			break;
		case(CLOSE_AUTHENTICATION):
			type = "Authentication Required" + " " + fireFoxDialogID;
			break;
		case(CLOSE_DOWNLOAD):
			type = "Opening" + " " + fireFoxDialogID;
			break;		
		}
		return type;
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
	
	public static void setExePath()
	{
		if( browserType.equals(BrowserType.chrome) )
			exePath = PopUpCancel.class.getProtectionDomain().getCodeSource().getLocation().getPath() + fileNameChrome;
		else if( browserType.equals(BrowserType.firefox) )
			exePath = PopUpCancel.class.getProtectionDomain().getCodeSource().getLocation().getPath() + fileNameFirefox;
		else
			exePath = PopUpCancel.class.getProtectionDomain().getCodeSource().getLocation().getPath() + fileNameFirefox;		
	}
	
	public static void setBrowserType(BrowserType ibrowserType)
	{
		browserType = ibrowserType;
		setExePath();
	}
	
	public static String getFilePath()
	{
		return exePath;
	}

}