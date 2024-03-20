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
 * Constructor test.
 * 
 * @author eselmeister
 */
public class Ion_7_Test extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testConstructor_1() {

		IIon ion = new Ion(5.5f);
		assertEquals(5.5d, ion.getIon());
	}

	public void testConstructor_2() {

		IIon ion = new Ion(-0.1f, 2593.5f);
		assertEquals(0d, ion.getIon());
		assertEquals(2593.5f, ion.getAbundance());
	}

	public void testConstructor_3() {

		IIon ion = new Ion(-0.1f);
		assertEquals(0d, ion.getIon());
	}

	public void testConstructor_4() {

		IIon ion = new Ion(1.0f, -0.1f);
		assertEquals(1d, ion.getIon());
		assertEquals(0f, ion.getAbundance());
	}
}
