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

import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import junit.framework.TestCase;

public class AbstractIon_1_Test extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetIon_1() {

		int ion = AbstractIon.getIon(45.4f);
		assertEquals("Ion", 45, ion);
	}

	public void testGetIon_2() {

		int ion = AbstractIon.getIon(0.0f);
		assertEquals("Ion", 0, ion);
	}

	public void testGetIon_3() {

		int ion = AbstractIon.getIon(45.5f);
		assertEquals("Ion", 46, ion);
	}

	public void testGetIon_4() {

		int ion = AbstractIon.getIon(45.7f);
		assertEquals("Ion", 46, ion);
	}

	public void testGetAbundance_1() {

		int abundance = AbstractIon.getAbundance(34345.4f);
		assertEquals("Abundance", 34345, abundance);
	}

	public void testGetAbundance_2() {

		int abundance = AbstractIon.getAbundance(0.0f);
		assertEquals("Abundance", 0, abundance);
	}

	public void testGetAbundance_3() {

		int abundance = AbstractIon.getAbundance(34345.5f);
		assertEquals("Abundance", 34346, abundance);
	}

	public void testGetAbundance_4() {

		int abundance = AbstractIon.getAbundance(34345.7f);
		assertEquals("Abundance", 34346, abundance);
	}
}
