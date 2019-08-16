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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PeakSampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Before;
import org.junit.Test;

public class PeakExtractionSupportTest_1_4 {

	private Samples samples;

	@Before
	public void setUp() {

		PeakExtractionSupport peakExtractionSupport = new PeakExtractionSupport(1);
		Map<IDataInputEntry, IPeaks> dataInput = new LinkedHashMap<>();
		TestSupport.putPeakDataToMap("Sample1", "Group1", new int[]{1, 2}, new double[]{3.2, 3.4}, dataInput);
		TestSupport.putPeakDataToMap("Sample2", "Group2", new int[]{1, 2}, new double[]{7, 10}, dataInput);
		TestSupport.putPeakDataToMap("Sample3", null, new int[]{1, 2, 3}, new double[]{2, 3, 4}, dataInput);
		samples = peakExtractionSupport.extractPeakData(dataInput, new NullProgressMonitor());
	}

	@Test
	public void testExtractPeakData() {

		/*
		 * check samples
		 */
		assertEquals(3, samples.getSampleList().size());
		assertEquals("Sample1", samples.getSampleList().get(0).getName());
		assertEquals("Group1", samples.getSampleList().get(0).getGroupName());
		assertEquals("Sample2", samples.getSampleList().get(1).getName());
		assertEquals("Group2", samples.getSampleList().get(1).getGroupName());
		assertEquals("Sample3", samples.getSampleList().get(2).getName());
		assertEquals(null, samples.getSampleList().get(2).getGroupName());
	}

	@Test
	public void testSampleSampleData() {

		/*
		 * check samples
		 */
		List<PeakSampleData> sampleData = samples.getSampleList().get(0).getSampleData();
		assertEquals(3, sampleData.size());
		assertEquals(3.2, sampleData.get(0).getData(), 0.0);
		assertEquals(3.2, sampleData.get(0).getModifiedData(), 0.0);
		assertEquals(3.4, sampleData.get(1).getData(), 0.0);
		assertEquals(3.4, sampleData.get(1).getModifiedData(), 0.0);
		assertEquals(Double.NaN, sampleData.get(2).getData(), 0.0);
		assertEquals(Double.NaN, sampleData.get(2).getModifiedData(), 0.0);
		//
		sampleData = samples.getSampleList().get(1).getSampleData();
		assertEquals(3, sampleData.size());
		assertEquals(7, sampleData.get(0).getData(), 0.0);
		assertEquals(7, sampleData.get(0).getModifiedData(), 0.0);
		assertEquals(10, sampleData.get(1).getData(), 0.0);
		assertEquals(10, sampleData.get(1).getModifiedData(), 0.0);
		assertEquals(Double.NaN, sampleData.get(2).getData(), 0.0);
		assertEquals(Double.NaN, sampleData.get(2).getModifiedData(), 0.0);
		//
		sampleData = samples.getSampleList().get(2).getSampleData();
		assertEquals(3, sampleData.size());
		assertEquals(2, sampleData.get(0).getData(), 0.0);
		assertEquals(2, sampleData.get(0).getModifiedData(), 0.0);
		assertEquals(3, sampleData.get(1).getData(), 0.0);
		assertEquals(3, sampleData.get(1).getModifiedData(), 0.0);
		assertEquals(4, sampleData.get(2).getData(), 0.0);
		assertEquals(4, sampleData.get(2).getModifiedData(), 0.0);
	}
}
