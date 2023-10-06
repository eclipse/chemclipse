/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.quantitation;

import org.eclipse.chemclipse.model.implementation.QuantitationEntry;

import junit.framework.TestCase;

public class QuantitationEntry_1_Test extends TestCase {

	private IQuantitationEntry quantitationEntry1;
	private IQuantitationEntry quantitationEntry2;
	private IQuantitationEntry quantitationEntry3;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		quantitationEntry1 = new QuantitationEntry("Toluene", "PAK", 0.25d, "mg/g", 2000.0d);
		quantitationEntry2 = new QuantitationEntry("Toluene", "", 0.25d, "mg/g", 2000.0d);
		quantitationEntry3 = new QuantitationEntry("Toluene", "PAK", 0.25d, "mg/g", 2000.0d);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertFalse(quantitationEntry1.equals(quantitationEntry2));
	}

	public void test2() {

		assertTrue(quantitationEntry1.equals(quantitationEntry3));
	}

	public void test3() {

		assertFalse(quantitationEntry2.equals(quantitationEntry3));
	}
}