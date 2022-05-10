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

import java.util.Set;

import org.eclipse.chemclipse.model.core.MarkedTraceModus;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;

import junit.framework.TestCase;

public class ExcludedIons_2_Test extends TestCase {

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

		assertFalse("contains", excludedIons.getIonsNominal().contains(AbstractIon.getIon(4.9f)));
	}

	public void testContains_2() {

		excludedIons.add(new MarkedIon(5));
		assertTrue("contains", excludedIons.getIonsNominal().contains(AbstractIon.getIon(4.9f)));
	}

	public void testContains_3() {

		excludedIons.add(new MarkedIon(5));
		excludedIons.remove(new MarkedIon(5));
		assertFalse("contains", excludedIons.getIonsNominal().contains(AbstractIon.getIon(5.4f)));
	}

	public void testContains_4() {

		excludedIons.add(new MarkedIon(10));
		excludedIons.add(new MarkedIon(5));
		excludedIons.add(new MarkedIon(20));
		Set<Integer> excludedIonsNominal = excludedIons.getIonsNominal();
		assertTrue("contains", excludedIonsNominal.contains(AbstractIon.getIon(20.2f)));
	}

	public void testContains_5() {

		excludedIons.add(50, 60);
		Set<Integer> list = excludedIons.getIonsNominal();
		assertEquals("size", 11, list.size());
		for(int i = 50; i <= 60; i++) {
			assertTrue("ion " + i, list.contains(i));
		}
	}
}