/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.quantitation.IQuantitationSignal;
import org.eclipse.chemclipse.model.quantitation.QuantitationSignal;

import junit.framework.TestCase;

public class QuantitationSignalMSD_1_Test extends TestCase {

	private IQuantitationSignal quantitationSignal;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		quantitationSignal = new QuantitationSignal(56.2d, 78.5f);
	}

	@Override
	protected void tearDown() throws Exception {

		quantitationSignal = null;
		super.tearDown();
	}

	public void testGetIon_1() {

		assertEquals(56.2d, quantitationSignal.getSignal());
	}

	public void testGetRelativeResponse_1() {

		assertEquals(78.5f, quantitationSignal.getRelativeResponse());
	}

	public void testGetUncertainty_1() {

		assertEquals(0.0d, quantitationSignal.getUncertainty());
	}

	public void testGetUncertainty_2() {

		quantitationSignal.setUncertainty(0.6d);
		assertEquals(0.6d, quantitationSignal.getUncertainty());
	}

	public void testIsUse_1() {

		assertEquals(true, quantitationSignal.isUse());
	}

	public void testIsUse_2() {

		quantitationSignal.setUse(false);
		assertEquals(false, quantitationSignal.isUse());
	}
}
