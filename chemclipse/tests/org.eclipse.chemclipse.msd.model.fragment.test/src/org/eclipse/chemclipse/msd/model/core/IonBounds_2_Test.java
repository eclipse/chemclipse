/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import org.eclipse.chemclipse.msd.model.implementation.Ion;

import junit.framework.TestCase;

/**
 * ion = new DefaultIon(25.5f, 3452.4f);
 * 
 * @author eselmeister
 */
public class IonBounds_2_Test extends TestCase {

	private IIonBounds ionBounds;
	private Ion ion;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ion = new Ion(25.5f, 3452.4f);
		ionBounds = new IonBounds(ion, ion);
	}

	@Override
	protected void tearDown() throws Exception {

		ion = null;
		ionBounds = null;
		super.tearDown();
	}

	public void testGetLowestIon_1() {

		assertEquals("GetLowestIon Ion", 25.5d, ionBounds.getLowestIon().getIon());
	}

	public void testGetLowestIon_2() {

		assertEquals("GetLowestIon Abundance", 3452.4f, ionBounds.getLowestIon().getAbundance());
	}

	public void testGetHighestIon_1() {

		assertEquals("GetHighestIon Ion", 25.5d, ionBounds.getHighestIon().getIon());
	}

	public void testGetHighestIon_2() {

		assertEquals("GetHighestIon Abundance", 3452.4f, ionBounds.getHighestIon().getAbundance());
	}
}
