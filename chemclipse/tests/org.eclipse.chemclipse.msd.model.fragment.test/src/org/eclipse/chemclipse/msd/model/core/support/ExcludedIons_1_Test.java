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

import junit.framework.TestCase;

public class ExcludedIons_1_Test extends TestCase {

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

		assertFalse("contains", excludedIons.getIonsNominal().contains(5));
	}

	public void testContains_2() {

		excludedIons.add(new MarkedIon(5));
		assertTrue("contains", excludedIons.getIonsNominal().contains(5));
	}

	public void testContains_3() {

		excludedIons.add(new MarkedIon(5));
		excludedIons.remove(new MarkedIon(5));
		assertFalse("contains", excludedIons.getIonsNominal().contains(5));
	}

	public void testContains_4() {

		excludedIons.add(new MarkedIon(10));
		excludedIons.add(new MarkedIon(5));
		excludedIons.add(new MarkedIon(20));
		assertTrue("contains", excludedIons.getIonsNominal().contains(20));
	}

	public void testContains_5() {

		excludedIons.add(10, 12);
		Set<Integer> excludedIonsNominal = excludedIons.getIonsNominal();
		assertFalse("contains", excludedIonsNominal.contains(20));
		assertTrue("contains", excludedIonsNominal.contains(10));
		assertTrue("contains", excludedIonsNominal.contains(11));
		assertTrue("contains", excludedIonsNominal.contains(12));
	}

	public void testContains_6() {

		excludedIons.add(12, 10);
		Set<Integer> excludedIonsNominal = excludedIons.getIonsNominal();
		assertFalse("contains", excludedIonsNominal.contains(20));
		assertTrue("contains", excludedIonsNominal.contains(10));
		assertTrue("contains", excludedIonsNominal.contains(11));
		assertTrue("contains", excludedIonsNominal.contains(12));
	}

	public void testContains_7() {

		excludedIons.add(12, 12);
		Set<Integer> excludedIonsNominal = excludedIons.getIonsNominal();
		assertFalse("contains", excludedIonsNominal.contains(20));
		assertFalse("contains", excludedIonsNominal.contains(10));
		assertFalse("contains", excludedIonsNominal.contains(11));
		assertTrue("contains", excludedIonsNominal.contains(12));
	}

	public void testSize_8() {

		excludedIons.add(12, 12);
		assertEquals("size", 1, excludedIons.getIonsNominal().size());
	}

	public void testSize_9() {

		excludedIons.add(new MarkedIon(58));
		excludedIons.add(new MarkedIon(48));
		excludedIons.add(new MarkedIon(372));
		assertEquals("size", 3, excludedIons.getIonsNominal().size());
	}

	public void testSize_10() {

		assertEquals("size", 0, excludedIons.getIonsNominal().size());
	}
}
