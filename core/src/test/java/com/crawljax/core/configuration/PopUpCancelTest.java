package com.crawljax.core.configuration;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PopUpCancelTest {

	@Test
	public void testSetMode() {

		PopUpCancel.setMode("DOWNLOAD");
		assertEquals(PopUpCancel.getMode(), "DOWNLOAD");
		PopUpCancel.setMode("ALL");
		assertEquals(PopUpCancel.getMode(), "ALL");

	}

	@Test
	public void testClosePopUps() {

		PopUpCancel.setMode("DOWNLOAD");
		
		System.out.println(PopUpCancel.getMode());
		PopUpCancel.ClosePopUps();
		System.out.println(PopUpCancel.getMode());
		//assertTrue(PopUpCancel.getMode().equals("NONE"));
	}
	
	@Test
	public void testKillExe() {
		// TODO
		// Run a dummy process
		// PopUpCancel.deleteTemp();
		// String myExePath=PopUpCancel.getFilePath();
		// assertEquals(myExePath,"Null");

	}


}