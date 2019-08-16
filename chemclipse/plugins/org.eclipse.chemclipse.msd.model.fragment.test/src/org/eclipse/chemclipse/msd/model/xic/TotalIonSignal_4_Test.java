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

import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.TotalScanSignal;

import junit.framework.TestCase;

/**
 * HashCode and equals test. totalIonSignal1 = new
 * TotalIonSignal(5720,1245.5f,25476.45f); totalIonSignal2 = new
 * TotalIonSignal(5720,1245.5f,25476.45f);
 * 
 * @author eselmeister
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

		assertTrue("equals", totalIonSignal1.equals(totalIonSignal2));
	}

	public void testEquals_2() {

		assertTrue("equals", totalIonSignal2.equals(totalIonSignal1));
	}

	public void testEquals_3() {

		assertFalse("equals", totalIonSignal1.equals(null));
	}

	public void testEquals_4() {

		assertFalse("equals", totalIonSignal2.equals(null));
	}

	public void testEquals_5() {

		assertTrue("equals", totalIonSignal1.equals(totalIonSignal1));
	}

	public void testEquals_6() {

		assertTrue("equals", totalIonSignal2.equals(totalIonSignal2));
	}

	public void testEquals_7() {

		assertFalse("equals", totalIonSignal1.equals(new String("Test")));
	}

	public void testEquals_8() {

		assertFalse("equals", totalIonSignal2.equals(new String("Test")));
	}
}
