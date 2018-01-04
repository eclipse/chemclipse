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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.result;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResults;

import junit.framework.TestCase;

public class PeakIntegrationResults_1_Test extends TestCase {

	private IPeakIntegrationResults results;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		results = new PeakIntegrationResults();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetPeakIntegrationResult_1() {

		assertNull(results.getPeakIntegrationResult(0));
	}

	public void testGetPeakIntegrationResultList_1() {

		List<IPeakIntegrationResult> presults = results.getPeakIntegrationResultList(55);
		assertEquals("size", 0, presults.size());
	}

	public void testGetPeakIntegrationResultThatContains_1() {

		List<IPeakIntegrationResult> presults = results.getPeakIntegrationResultThatContains(55);
		assertEquals("size", 0, presults.size());
	}

	public void testGetSize_1() {

		assertEquals("size", 0, results.size());
	}
}
