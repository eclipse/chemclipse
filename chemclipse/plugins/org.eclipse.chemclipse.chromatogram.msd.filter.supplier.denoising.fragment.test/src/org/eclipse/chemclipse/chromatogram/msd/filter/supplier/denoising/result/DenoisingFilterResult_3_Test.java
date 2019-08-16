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
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.result;

import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;

import junit.framework.TestCase;

public class DenoisingFilterResult_3_Test extends TestCase {

	private IDenoisingFilterResult result;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		result = new DenoisingFilterResult(ResultStatus.OK, "The result status is ok.", null);
	}

	@Override
	protected void tearDown() throws Exception {

		result = null;
		super.tearDown();
	}

	public void testGetNoiseMassSpectra_1() {

		assertNotNull(result.getNoiseMassSpectra());
	}

	public void testGetNoiseMassSpectra_2() {

		assertEquals("Size", 0, result.getNoiseMassSpectra().size());
	}
}
