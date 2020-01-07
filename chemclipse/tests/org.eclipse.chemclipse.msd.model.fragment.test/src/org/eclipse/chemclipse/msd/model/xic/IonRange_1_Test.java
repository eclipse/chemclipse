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
package org.eclipse.chemclipse.msd.model.xic;

import junit.framework.TestCase;

/**
 * Tests the class IonRange.
 * 
 * @author eselmeister
 */
public class IonRange_1_Test extends TestCase {

	private IIonRange ionRange;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		ionRange = null;
		super.tearDown();
	}

	public void testSetup_1() {

		ionRange = new IonRange(1, 5);
		assertEquals("startIon", 1, ionRange.getStartIon());
		assertEquals("stopIon", 5, ionRange.getStopIon());
	}

	public void testSetup_2() {

		ionRange = new IonRange(5, 1);
		assertEquals("startIon", 1, ionRange.getStartIon());
		assertEquals("stopIon", 5, ionRange.getStopIon());
	}

	public void testSetup_3() {

		ionRange = new IonRange(0, 5);
		assertEquals("startIon", IIonRange.MIN_ION, ionRange.getStartIon());
		assertEquals("stopIon", 5, ionRange.getStopIon());
	}

	public void testSetup_4() {

		ionRange = new IonRange(1, 0);
		assertEquals("startIon", 1, ionRange.getStartIon());
		// Why MIN_Ion? Because 1 is greater than 0 so the values will be
		// swapped. 0 is now the startIon.
		assertEquals("stopIon", IIonRange.MIN_ION, ionRange.getStopIon());
	}

	public void testSetup_5() {

		ionRange = new IonRange(IIonRange.MAX_ION + 1, 5);
		assertEquals("startIon", 5, ionRange.getStartIon());
		// Why MAX_Ion? Because MAX_Ion+1 is greater than 5 so the values will be
		// swapped. 5 is now the startIon.
		assertEquals("stopIon", IIonRange.MAX_ION, ionRange.getStopIon());
	}

	public void testSetup_6() {

		ionRange = new IonRange(1, IIonRange.MAX_ION + 1);
		assertEquals("startIon", 1, ionRange.getStartIon());
		assertEquals("stopIon", IIonRange.MAX_ION, ionRange.getStopIon());
	}
}
