/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model;

import junit.framework.TestCase;

public class WncIons_1_Test extends TestCase {

	private IWncIon wncIon;
	private IWncIons wncIons;

	@Override
	protected void setUp() throws Exception {

		wncIons = new WncIons();
		wncIon = new WncIon(18, "Water");
		wncIons.add(wncIon);
		wncIon = new WncIon(28, "Nitrogen");
		wncIons.add(wncIon);
		wncIon = new WncIon(44, "Carbon dioxide");
		wncIons.add(wncIon);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		wncIons = null;
		super.tearDown();
	}

	public void testWNCIons_1() {

		assertEquals(3, wncIons.getKeys().size());
	}

	public void testWNCIons_2() {

		wncIon = wncIons.getWNCIon(18);
		assertEquals(18, wncIon.getIon());
		assertEquals("Water", wncIon.getName());
	}

	public void testWNCIons_3() {

		wncIon = wncIons.getWNCIon(28);
		assertEquals(28, wncIon.getIon());
		assertEquals("Nitrogen", wncIon.getName());
	}

	public void testWNCIons_4() {

		wncIon = wncIons.getWNCIon(44);
		assertEquals(44, wncIon.getIon());
		assertEquals("Carbon dioxide", wncIon.getName());
	}

	public void testWNCIons_5() {

		wncIon = wncIons.getWNCIon(45);
		assertNull(wncIon);
	}

	public void testWNCIons_6() {

		wncIons.remove(18);
		wncIons.remove(28);
		assertEquals(1, wncIons.getKeys().size());
		wncIon = wncIons.getWNCIon(44);
		assertEquals(44, wncIon.getIon());
		assertEquals("Carbon dioxide", wncIon.getName());
	}

	public void testWNCIons_7() {

		wncIons.remove(18);
		wncIons.remove(28);
		assertEquals(1, wncIons.getKeys().size());
		wncIon = wncIons.getWNCIon(18);
		assertNull(wncIon);
		wncIon = wncIons.getWNCIon(28);
		assertNull(wncIon);
	}

	public void testWNCIons_8() {

		wncIons.remove(18);
		wncIons.remove(28);
		wncIons.remove(44);
		assertEquals(0, wncIons.getKeys().size());
	}
}
