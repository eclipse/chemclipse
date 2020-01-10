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
package org.eclipse.chemclipse.msd.model.core;

import org.eclipse.chemclipse.msd.model.core.IIonBounds;
import org.eclipse.chemclipse.msd.model.core.IonBounds;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import junit.framework.TestCase;

/**
 * ion1 = new DefaultIon(25.5f, 3452.4f); ion2 = new
 * DefaultIon(27.2f, 4785.6f);
 * 
 * @author eselmeister
 */
public class IonBounds_3_Test extends TestCase {

	private IIonBounds ionBounds;
	private Ion ion1;
	private Ion ion2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ion1 = new Ion(25.5f, 3452.4f);
		ion2 = new Ion(27.2f, 4785.6f);
		ionBounds = new IonBounds(ion1, ion2);
	}

	@Override
	protected void tearDown() throws Exception {

		ion1 = null;
		ion2 = null;
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

		assertEquals("GetHighestIon Ion", 27.200000762939453d, ionBounds.getHighestIon().getIon());
	}

	public void testGetHighestIon_2() {

		assertEquals("GetHighestIon Abundance", 4785.6f, ionBounds.getHighestIon().getAbundance());
	}
}
