/*******************************************************************************
 * Copyright (c) 2014, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - update to reflect changes in INoiseCalculator API
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.noise.stein.core;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.noise.stein.TestPathHelper;
import org.eclipse.chemclipse.model.results.ChromatogramSegmentation;

public class NoiseCalculator_2_ITest extends ChromatogramReaderTestCase {

	private NoiseCalculator noiseCalculator;

	@Override
	protected void setUp() throws Exception {

		pathImport = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_2);
		super.setUp();
		noiseCalculator = new NoiseCalculator();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testReader_1() {

		chromatogram.addMeasurementResult(new ChromatogramSegmentation(chromatogram, 9));
		assertEquals(236, noiseCalculator.getSignalToNoiseRatio(chromatogram, 500));
	}
}
