/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.linear.RealMatrix;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;
import org.junit.Before;
import org.junit.Test;

public class PcaUtilsTest4 {

	private Samples samples;

	@Before
	public void setUp() {

		String[][] samples = {{"Sample1", "Group1"}, //
				{"Sample2", "Group2"}, //
				{"Sample3", "Group3"}, //
				{"Sample4", "Group3"}};
		int[] variables = {10, 20, 30, 40, 50};
		double[][] data = {{1.2, 3.5, 4.6, 0.9, 9.8}, //
				{1.3, 2.5, 5.6, 0.5, 10.8}, //
				{0.5, 3.2, 9.0, 1.9, 7.8}, //
				{1.4, 3.9, 4.8, 0.8, 9.7}};
		boolean[] selected = {true, false, true, true};
		this.samples = TestSupport.createSamples(samples, selected, variables, data);
	}

	@Test
	public void testExtractData() {

		Map<String, double[]> output = PcaUtils.extractData(samples);
		assertEquals(3, output.size());
		assertTrue(output.containsKey("Sample1"));
		assertFalse(output.containsKey("Sample2"));
		assertTrue(output.containsKey("Sample3"));
		assertTrue(output.containsKey("Sample4"));
		for(double[] data : output.values()) {
			assertEquals(5, data.length);
		}
	}

	@Test
	public void testGetCovarianceMatrix() {

		RealMatrix realMatrix = PcaUtils.getCovarianceMatrix(samples);
		assertEquals(5, realMatrix.getColumnDimension());
		assertEquals(5, realMatrix.getRowDimension());
	}

	@Test
	public void testGetGroupNamesListOfSBoolean() {

		Set<String> groups = PcaUtils.getGroupNames(samples.getSampleList(), false);
		assertEquals(3, groups.size());
		assertTrue(groups.contains("Group1"));
		assertTrue(groups.contains("Group2"));
		assertTrue(groups.contains("Group3"));
	}

	@Test
	public void testGetGroupNamesListOfSBoolean2() {

		Set<String> groups = PcaUtils.getGroupNames(samples.getSampleList(), true);
		assertEquals(2, groups.size());
		assertTrue(groups.contains("Group1"));
		assertFalse(groups.contains("Group2"));
		assertTrue(groups.contains("Group3"));
	}
}
