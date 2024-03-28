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
 * Exception test.
 * 
 * @author eselmeister
 */
public class Ion_6_Test extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testException_1() {

		IIon ion = new Ion(IIon.TIC_ION, -1.0f);
		assertEquals(0d, ion.getIon());
		assertEquals(0f, ion.getAbundance());
	}

	public void testException_2() {

		IIon ion = new Ion(IIon.TIC_ION, 1.0f);
		assertFalse(ion.setAbundance(-1.0f));
	}

	public void testException_3() {

		IIon ion = new Ion(-1.0f, 1.0f);
		assertEquals(0d, ion.getIon());
		assertEquals(1.0f, ion.getAbundance());
	}

	public void testException_4() {

		Ion ion = new Ion(1.0f, 1.0f);
		assertFalse(ion.setIon(-1.0f));
	}
}
