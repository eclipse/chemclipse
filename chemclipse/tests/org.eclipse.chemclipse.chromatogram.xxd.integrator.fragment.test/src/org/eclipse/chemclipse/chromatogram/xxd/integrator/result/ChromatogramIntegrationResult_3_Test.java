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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.result;

import junit.framework.TestCase;

public class ChromatogramIntegrationResult_3_Test extends TestCase {

	private IChromatogramIntegrationResult result;
	private float ion;
	private double backgroundArea;
	private double chromatogramArea;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ion = 28.2f;
		backgroundArea = -10020993.34d;
		chromatogramArea = -289839830.483d;
		result = new ChromatogramIntegrationResult(ion, chromatogramArea, backgroundArea);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetBackgroundArea_1() {

		assertEquals("BackgroundArea", backgroundArea, result.getBackgroundArea());
	}

	public void testGetChromatogramArea_1() {

		assertEquals("ChromatogramArea", chromatogramArea, result.getChromatogramArea());
	}

	public void testGetIon_1() {

		assertEquals("Ion", 28.200000762939453d, result.getIon());
	}
}
