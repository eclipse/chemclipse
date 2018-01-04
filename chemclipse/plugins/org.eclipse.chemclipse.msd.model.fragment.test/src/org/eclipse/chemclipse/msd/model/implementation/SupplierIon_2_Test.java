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
package org.eclipse.chemclipse.msd.model.implementation;

import junit.framework.TestCase;

/**
 * IgnoreAbundanceLimit test.
 * 
 * @author eselmeister
 */
public class SupplierIon_2_Test extends TestCase {

	private ScanIon ion;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ion = new ScanIon(1.0f, true);
	}

	@Override
	protected void tearDown() throws Exception {

		ion = null;
		super.tearDown();
	}

	public void testIgnoreAbundanceLimit_1() {

		assertEquals("IgnoreAbundanceLimit", true, ion.isIgnoreAbundanceLimit());
	}

	public void testIgnoreAbundanceLimit_2() {

		assertEquals("IgnoreAbundanceLimit", true, ion.isIgnoreAbundanceLimit());
		ion.setIgnoreAbundanceLimit(false);
		assertEquals("IgnoreAbundanceLimit", false, ion.isIgnoreAbundanceLimit());
		ion.setIgnoreAbundanceLimit(true);
		assertEquals("IgnoreAbundanceLimit", true, ion.isIgnoreAbundanceLimit());
	}
}
