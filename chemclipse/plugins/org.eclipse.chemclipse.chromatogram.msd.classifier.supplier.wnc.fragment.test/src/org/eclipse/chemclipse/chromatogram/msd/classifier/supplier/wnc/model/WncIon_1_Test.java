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

public class WncIon_1_Test extends TestCase {

	private IWncIon wncIon;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testCreateWNCIon_1() {

		String name = "Water";
		int ion = 18;
		wncIon = new WncIon(ion, name);
		assertEquals(ion, wncIon.getIon());
		assertEquals(name, wncIon.getName());
	}

	public void testCreateWNCIon_2() {

		String name = "Nitrogen";
		int ion = 28;
		wncIon = new WncIon(ion, name);
		assertEquals(ion, wncIon.getIon());
		assertEquals(name, wncIon.getName());
	}

	public void testCreateWNCIon_3() {

		String name = "Nitro;gen";
		int ion = 28;
		wncIon = new WncIon(ion, name);
		assertEquals(ion, wncIon.getIon());
		assertEquals("Nitrogen", wncIon.getName());
	}

	public void testCreateWNCIon_4() {

		String name = "Nitro:gen";
		int ion = 28;
		wncIon = new WncIon(ion, name);
		assertEquals(ion, wncIon.getIon());
		assertEquals("Nitrogen", wncIon.getName());
	}

	public void testCreateWNCIon_5() {

		String name = "    Ni;tro:gen";
		int ion = 28;
		wncIon = new WncIon(ion, name);
		assertEquals(ion, wncIon.getIon());
		assertEquals("Nitrogen", wncIon.getName());
	}

	public void testCreateWNCIon_6() {

		String name = "    Ni;tro:  gen    ";
		int ion = 28;
		wncIon = new WncIon(ion, name);
		assertEquals(ion, wncIon.getIon());
		assertEquals("Nitro  gen", wncIon.getName());
	}
}
