/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.ScansExtractionSupport.ExtractionType;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.statistics.RetentionTime;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Before;
import org.junit.Test;

public class ScansExtractionSupportTest4_4 {

	private ScansExtractionSupport scansExtractionSupport;
	private Samples samples;

	@Before
	public void setUp() throws Exception {

		scansExtractionSupport = new ScansExtractionSupport(1, 50, ExtractionType.CLOSEST_SCAN, true);
		Map<IDataInputEntry, Collection<IScan>> dataInput = new LinkedHashMap<>();
		TestSupport.putScanDataToMap("Sample3", null, new int[]{1, 21, 30}, new float[]{2f, 3f, 4f}, dataInput);
		TestSupport.putScanDataToMap("Sample1", "Group1", new int[]{1, 20}, new float[]{3.2f, 3.4f}, dataInput);
		TestSupport.putScanDataToMap("Sample2", "Group2", new int[]{1, 20}, new float[]{7f, 10f}, dataInput);
		samples = scansExtractionSupport.process(dataInput, new NullProgressMonitor());
	}

	@Test
	public void testScansExtractionSupport() {

		/**
		 * check variable
		 */
		List<RetentionTime> variables = samples.getVariables();
		assertEquals(20, variables.size());
	}
}
