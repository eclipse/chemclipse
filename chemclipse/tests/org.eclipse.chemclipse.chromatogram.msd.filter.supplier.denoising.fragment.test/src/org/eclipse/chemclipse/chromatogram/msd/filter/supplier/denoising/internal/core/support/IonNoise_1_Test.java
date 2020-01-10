/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.internal.core.support;

import junit.framework.TestCase;

public class IonNoise_1_Test extends TestCase {

	private IonNoise ionNoise;

	@Override
	protected void setUp() throws Exception {

		ionNoise = new IonNoise(167, 5893.56f);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetIon_1() {

		assertEquals("Ion", 167, ionNoise.getIon());
	}

	public void testGetAbundance_1() {

		assertEquals("Abundance", 5893.56f, ionNoise.getAbundance());
	}
}
