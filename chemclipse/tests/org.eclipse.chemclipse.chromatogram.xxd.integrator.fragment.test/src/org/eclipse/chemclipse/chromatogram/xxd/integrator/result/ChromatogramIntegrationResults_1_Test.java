/*******************************************************************************
 * Copyright (c) 2011, 2021 Lablicate GmbH.
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

public class ChromatogramIntegrationResults_1_Test extends TestCase {

	private IChromatogramIntegrationResults results;
	private IChromatogramIntegrationResult result;
	private float ion;
	private double backgroundArea;
	private double chromatogramArea;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		results = new ChromatogramIntegrationResults();
		ion = 28.2f;
		backgroundArea = 10020993.34d;
		chromatogramArea = 289839830.483d;
		result = new ChromatogramIntegrationResult(ion, chromatogramArea, backgroundArea);
		results.add(result);
		ion = 42.5f;
		backgroundArea = 2810.2349d;
		chromatogramArea = 3466.4654d;
		result = new ChromatogramIntegrationResult(ion, chromatogramArea, backgroundArea);
		results.add(result);
		ion = 18.1f;
		backgroundArea = 7823090.9d;
		chromatogramArea = 23938.54d;
		result = new ChromatogramIntegrationResult(ion, chromatogramArea, backgroundArea);
		results.add(result);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetBackgroundArea_1() {

		assertEquals("BackgroundArea", 17846894.4749d, results.getTotalBackgroundArea());
	}

	public void testGetChromatogramArea_1() {

		assertEquals("ChromatogramArea", 289867235.4884d, results.getTotalChromatogramArea());
	}
}
