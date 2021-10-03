/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
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

import static org.junit.Assert.assertNotEquals;

import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.TotalScanSignal;

import junit.framework.TestCase;

/**
 * HashCode and equals test. totalIonSignal1 = new
 * TotalIonSignal(5720,1245.5f,25476.45f); totalIonSignal2 = new
 * TotalIonSignal(5720,1245.5f,25476.45f);
 * 
 * @author Philip Wenig
 */
public class TotalIonSignal_4_Test extends TestCase {

	private ITotalScanSignal totalIonSignal1;
	private ITotalScanSignal totalIonSignal2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		totalIonSignal1 = new TotalScanSignal(5720, 1245.5f, 25476.45f);
		totalIonSignal2 = new TotalScanSignal(5720, 1245.5f, 25476.45f);
	}

	@Override
	protected void tearDown() throws Exception {

		totalIonSignal1 = null;
		totalIonSignal2 = null;
		super.tearDown();
	}

	public void testHashCode_1() {

		assertEquals("hashCode", totalIonSignal1.hashCode(), totalIonSignal2.hashCode());
	}

	public void testHashCode_2() {

		assertEquals("hashCode", totalIonSignal2.hashCode(), totalIonSignal1.hashCode());
	}

	public void testEquals_1() {

		assertEquals("equals", totalIonSignal1, totalIonSignal2);
	}

	public void testEquals_2() {

		assertEquals("equals", totalIonSignal2, totalIonSignal1);
	}

	public void testEquals_3() {

		assertNotNull("equals", totalIonSignal1);
	}

	public void testEquals_4() {

		assertNotNull("equals", totalIonSignal2);
	}

	public void testEquals_5() {

		assertEquals("equals", totalIonSignal1, totalIonSignal1);
	}

	public void testEquals_6() {

		assertEquals("equals", totalIonSignal2, totalIonSignal2);
	}

	public void testEquals_7() {

		assertNotEquals("equals", totalIonSignal1, new Object());
	}

	public void testEquals_8() {

		assertNotEquals("equals", totalIonSignal2, new Object());
	}
}
