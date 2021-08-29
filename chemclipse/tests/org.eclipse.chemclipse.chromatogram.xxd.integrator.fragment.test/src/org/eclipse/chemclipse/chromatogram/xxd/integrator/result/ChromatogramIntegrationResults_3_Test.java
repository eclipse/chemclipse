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

public class ChromatogramIntegrationResults_3_Test extends TestCase {

	private IChromatogramIntegrationResults results;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		results = new ChromatogramIntegrationResults();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetBackgroundArea_1() {

		assertEquals("BackgroundArea", 0.0d, results.getTotalBackgroundArea());
	}

	public void testGetChromatogramArea_1() {

		assertEquals("ChromatogramArea", 0.0d, results.getTotalChromatogramArea());
	}
}
