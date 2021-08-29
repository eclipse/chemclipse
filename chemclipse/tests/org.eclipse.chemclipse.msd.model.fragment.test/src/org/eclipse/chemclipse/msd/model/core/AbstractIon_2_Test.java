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

import junit.framework.TestCase;

public class AbstractIon_2_Test extends TestCase {

	private double exactIon;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		exactIon = 28.78749204f;
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetIon_1() {

		double ion = AbstractIon.getIon(exactIon, 1);
		assertEquals("Ion", 28.8d, ion);
	}

	public void testGetIon_2() {

		double ion = AbstractIon.getIon(exactIon, 2);
		assertEquals("Ion", 28.79d, ion);
	}

	public void testGetIon_3() {

		double ion = AbstractIon.getIon(exactIon, 3);
		assertEquals("Ion", 28.787d, ion);
	}

	public void testGetIon_4() {

		double ion = AbstractIon.getIon(exactIon, 4);
		assertEquals("Ion", 28.7875d, ion);
	}

	public void testGetIon_5() {

		double ion = AbstractIon.getIon(exactIon, 5);
		assertEquals("Ion", 28.78749d, ion);
	}

	public void testGetIon_6() {

		double ion = AbstractIon.getIon(exactIon, 6);
		assertEquals("Ion", 28.787493d, ion);
	}

	public void testGetIon_7() {

		double ion = AbstractIon.getIon(exactIon, 0);
		assertEquals("Ion", 28.8d, ion);
	}

	public void testGetIon_8() {

		double ion = AbstractIon.getIon(exactIon, -1);
		assertEquals("Ion", 28.8d, ion);
	}

	public void testGetIon_9() {

		double ion = AbstractIon.getIon(exactIon, 7);
		assertEquals("Ion", 28.8d, ion);
	}
}
