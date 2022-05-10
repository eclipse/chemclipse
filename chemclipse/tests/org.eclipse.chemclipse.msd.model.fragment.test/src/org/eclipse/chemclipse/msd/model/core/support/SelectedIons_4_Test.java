/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.support;

import org.eclipse.chemclipse.model.core.MarkedTraceModus;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;

import junit.framework.TestCase;

public class SelectedIons_4_Test extends TestCase {

	private IMarkedIons selectedIons;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		selectedIons = new MarkedIons(MarkedTraceModus.INCLUDE);
	}

	@Override
	protected void tearDown() throws Exception {

		selectedIons = null;
		super.tearDown();
	}

	public void testContains_1() {

		assertFalse("contains", selectedIons.getIonsNominal().contains(AbstractIon.getIon(5.2f)));
	}

	public void testContains_2() {

		selectedIons.add(new MarkedIon(5.2f));
		assertTrue("contains", selectedIons.getIonsNominal().contains(AbstractIon.getIon(5.3f)));
	}

	public void testContains_3() {

		selectedIons.add(new MarkedIon(5.2f));
		selectedIons.remove(new MarkedIon(5.3f));
		assertTrue("contains", selectedIons.getIonsNominal().contains(AbstractIon.getIon(5.0f)));
	}

	public void testContains_4() {

		selectedIons.add(new MarkedIon(10.2f));
		selectedIons.add(new MarkedIon(5.3f));
		selectedIons.add(new MarkedIon(20.4f));
		assertTrue("contains", selectedIons.getIonsNominal().contains(AbstractIon.getIon(20.4f)));
	}

	public void testSize_9() {

		selectedIons.add(new MarkedIon(58.3f));
		selectedIons.add(new MarkedIon(48.2f));
		selectedIons.add(new MarkedIon(372.4f));
		assertEquals("size", 3, selectedIons.getIonsNominal().size());
	}

	public void testSize_10() {

		assertEquals("size", 0, selectedIons.getIonsNominal().size());
	}
}
