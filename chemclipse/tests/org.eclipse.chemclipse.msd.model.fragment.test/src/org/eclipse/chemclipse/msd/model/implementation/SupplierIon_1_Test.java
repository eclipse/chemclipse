/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
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

import org.eclipse.chemclipse.msd.model.core.IIon;

import junit.framework.TestCase;

/**
 * Constructor test. TODO: add actual checks/verifications
 * 
 * @author eselmeister
 */
public class SupplierIon_1_Test extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testConstructor_1() {

		new Ion(0.5f);
	}

	public void testConstructor_2() {

		new Ion(0.4f);
	}

	public void testConstructor_3() {

		new Ion(65535.0f);
	}

	public void testConstructor_4() {

		new Ion(65535.1f);
	}

	public void testConstructor_5() {

		IIon ion = new Ion(1.0f);
		ion.setIon(0.4f);
	}

	public void testConstructor_6() {

		IIon ion = new Ion(1.0f);
		ion.setIon(65535.0f);
	}

	public void testConstructor_7() {

		IIon ion = new Ion(1.0f);
		ion.setIon(65535.1f);
	}

	public void testConstructor_8() {

		new Ion(0.5f, 0.0f);
	}

	public void testConstructor_9() {

		new Ion(65535.0f, 0.0f);
	}

	public void testConstructor_10() {

		new Ion(65535.1f, 0.0f);
	}

	public void testConstructor_11() {

		new Ion(0.4f, 0.0f);
	}

	public void testConstructor_12() {

		new Ion(0.5f, -0.1f);
	}

	public void testConstructor_13() {

		IIon ion = new Ion(0.5f, 0.0f);
		ion.setAbundance(-0.1f);
	}

	public void testConstructor_14() {

		IIon ion = new Ion(0.5f, 0.1f);
		ion.setIon(0.4f);
	}
}
