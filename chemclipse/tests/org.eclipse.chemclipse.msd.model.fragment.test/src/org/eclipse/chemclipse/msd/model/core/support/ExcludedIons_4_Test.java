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

public class ExcludedIons_4_Test extends TestCase {

	private IMarkedIons excludedIons;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		excludedIons = new MarkedIons(MarkedTraceModus.INCLUDE);
	}

	@Override
	protected void tearDown() throws Exception {

		excludedIons = null;
		super.tearDown();
	}

	public void testContains_1() {

		assertFalse("contains", excludedIons.getIonsNominal().contains(AbstractIon.getIon(5.0f)));
	}

	public void testContains_2() {

		excludedIons.add(new MarkedIon(5.2f));
		assertTrue("contains", excludedIons.getIonsNominal().contains(AbstractIon.getIon(5.0f)));
	}

	public void testContains_3() {

		excludedIons.add(new MarkedIon(5.3f));
		excludedIons.remove(new MarkedIon(5.2f));
		assertTrue("contains", excludedIons.getIonsNominal().contains(AbstractIon.getIon(5.0f)));
	}

	public void testContains_4() {

		excludedIons.add(new MarkedIon(10.4f));
		excludedIons.add(new MarkedIon(5.3f));
		excludedIons.add(new MarkedIon(20.2f));
		assertTrue("contains", excludedIons.getIonsNominal().contains(AbstractIon.getIon(20.3f)));
	}

	public void testSize_9() {

		excludedIons.add(new MarkedIon(58.4f));
		excludedIons.add(new MarkedIon(48.3f));
		excludedIons.add(new MarkedIon(372.2f));
		assertEquals("size", 3, excludedIons.getIonsNominal().size());
	}

	public void testSize_10() {

		assertEquals("size", 0, excludedIons.getIonsNominal().size());
	}
}
