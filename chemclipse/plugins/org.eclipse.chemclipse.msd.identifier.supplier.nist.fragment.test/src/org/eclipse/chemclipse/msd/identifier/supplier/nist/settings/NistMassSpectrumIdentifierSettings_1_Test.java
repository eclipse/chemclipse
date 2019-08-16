/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.settings;

import junit.framework.TestCase;

public class NistMassSpectrumIdentifierSettings_1_Test extends TestCase {

	private MassSpectrumIdentifierSettings settings;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		settings = new MassSpectrumIdentifierSettings();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetNumberOfTargets_1() {

		assertEquals(3, settings.getNumberOfTargets());
	}

	public void testGetNumberOfTargets_2() {

		settings.setNumberOfTargets(1);
		assertEquals(1, settings.getNumberOfTargets());
	}

	public void testGetNumberOfTargets_3() {

		settings.setNumberOfTargets(100);
		assertEquals(100, settings.getNumberOfTargets());
	}

	public void testGetNumberOfTargets_4() {

		settings.setNumberOfTargets(0);
		assertEquals(3, settings.getNumberOfTargets());
	}

	public void testGetNumberOfTargets_5() {

		settings.setNumberOfTargets(101);
		assertEquals(3, settings.getNumberOfTargets());
	}
}
