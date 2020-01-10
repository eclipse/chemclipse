/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.history;

import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.chemclipse.support.history.EditInformation;
import org.eclipse.chemclipse.support.history.IEditInformation;

public class EditInformation_2_Test extends TestCase {

	private IEditInformation editInformation;
	private Date date;
	private final String entry = "I have modified the chromatogram.";

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		date = new Date();
		editInformation = new EditInformation(entry);
	}

	@Override
	protected void tearDown() throws Exception {

		editInformation = null;
		super.tearDown();
	}

	public void testGetEditHistory_1() {

		long millisecondsEdit = editInformation.getDate().getTime();
		long millisecondsCheck = date.getTime();
		long delta = millisecondsCheck - millisecondsEdit;
		assertTrue("getDate", delta > -100 && delta < 100); // Sometimes the time deviates in the check by 100 ms
	}

	public void testGetEditHistory_2() {

		assertEquals("getDescription", entry, editInformation.getDescription());
	}
}
