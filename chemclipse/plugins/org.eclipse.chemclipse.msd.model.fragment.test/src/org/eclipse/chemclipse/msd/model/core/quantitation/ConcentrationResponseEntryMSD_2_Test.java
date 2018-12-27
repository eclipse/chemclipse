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

import org.eclipse.chemclipse.model.quantitation.ConcentrationResponseEntry;
import org.eclipse.chemclipse.model.quantitation.IConcentrationResponseEntry;

import junit.framework.TestCase;

public class ConcentrationResponseEntryMSD_2_Test extends TestCase {

	private IConcentrationResponseEntry concentrationResponseEntry1;
	private IConcentrationResponseEntry concentrationResponseEntry2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		concentrationResponseEntry1 = new ConcentrationResponseEntry(76.2d, 0.7d, 47875);
		concentrationResponseEntry2 = new ConcentrationResponseEntry(76.2d, 0.7d, 47875);
	}

	@Override
	protected void tearDown() throws Exception {

		concentrationResponseEntry1 = null;
		concentrationResponseEntry2 = null;
		super.tearDown();
	}

	public void testEquals_1() {

		assertTrue(concentrationResponseEntry1.equals(concentrationResponseEntry2));
	}

	public void testEquals_2() {

		assertTrue(concentrationResponseEntry2.equals(concentrationResponseEntry1));
	}

	public void testHashCode_1() {

		assertEquals(concentrationResponseEntry1.hashCode(), concentrationResponseEntry2.hashCode());
	}

	public void testHashCode_2() {

		assertEquals(concentrationResponseEntry2.hashCode(), concentrationResponseEntry1.hashCode());
	}

	public void testToString_1() {

		assertEquals(concentrationResponseEntry1.toString(), concentrationResponseEntry2.toString());
	}

	public void testToString_2() {

		assertEquals(concentrationResponseEntry2.toString(), concentrationResponseEntry1.toString());
	}
}
