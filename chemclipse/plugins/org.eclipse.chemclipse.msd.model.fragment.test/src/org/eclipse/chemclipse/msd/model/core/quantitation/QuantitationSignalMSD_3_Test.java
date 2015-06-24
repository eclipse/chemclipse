/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.quantitation;

import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationSignalMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.QuantitationSignalMSD;

import junit.framework.TestCase;

public class QuantitationSignalMSD_3_Test extends TestCase {

	private IQuantitationSignalMSD quantitationSignal1;
	private IQuantitationSignalMSD quantitationSignal2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		quantitationSignal1 = new QuantitationSignalMSD(56.2d, 78.5f);
		quantitationSignal2 = new QuantitationSignalMSD(55.2d, 78.5f);
	}

	@Override
	protected void tearDown() throws Exception {

		quantitationSignal1 = null;
		quantitationSignal2 = null;
		super.tearDown();
	}

	public void testEquals_1() {

		assertFalse(quantitationSignal1.equals(quantitationSignal2));
	}

	public void testEquals_2() {

		assertFalse(quantitationSignal2.equals(quantitationSignal1));
	}

	public void testHashCode_1() {

		assertTrue(quantitationSignal1.hashCode() != quantitationSignal2.hashCode());
	}

	public void testHashCode_2() {

		assertTrue(quantitationSignal2.hashCode() != quantitationSignal1.hashCode());
	}

	public void testToString_1() {

		assertTrue(quantitationSignal1.toString() != quantitationSignal2.toString());
	}

	public void testToString_2() {

		assertTrue(quantitationSignal2.toString() != quantitationSignal1.toString());
	}
}
