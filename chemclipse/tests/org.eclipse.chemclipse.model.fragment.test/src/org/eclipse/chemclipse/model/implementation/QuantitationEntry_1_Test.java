/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;

import junit.framework.TestCase;

public class QuantitationEntry_1_Test extends TestCase {

	private IQuantitationEntry quantitationEntry = new QuantitationEntry("Styrene", 10.0d, "mg/kg", 5000.8d);

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals("", quantitationEntry.getDescription());
	}

	public void test2() {

		quantitationEntry.appendDescription(null);
		assertEquals("", quantitationEntry.getDescription());
	}

	public void test3() {

		quantitationEntry.appendDescription("");
		assertEquals("", quantitationEntry.getDescription());
	}

	public void test4() {

		quantitationEntry.appendDescription("Test");
		assertEquals("Test", quantitationEntry.getDescription());
	}

	public void test5() {

		quantitationEntry.appendDescription("Test");
		quantitationEntry.appendDescription("Test");
		assertEquals("Test", quantitationEntry.getDescription());
	}

	public void test6() {

		quantitationEntry.appendDescription("Test");
		quantitationEntry.appendDescription("Demo");
		assertEquals("Test | Demo", quantitationEntry.getDescription());
	}

	public void test7() {

		quantitationEntry.appendDescription("Test");
		quantitationEntry.appendDescription("Demo");
		quantitationEntry.appendDescription("Test");
		assertEquals("Test | Demo", quantitationEntry.getDescription());
	}
}
