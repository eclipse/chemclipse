/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.chromatogram;

import org.eclipse.chemclipse.converter.chromatogram.IChromatogramConverterSupport;
import org.eclipse.chemclipse.converter.core.IConverterSupport;

import junit.framework.TestCase;

/**
 * Testing the method getChromatogramConverterSupport() in
 * ChromatogramConverter.
 */
public class ChromatogramConverter_2_Test extends TestCase {

	private IChromatogramConverterSupport support;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		support = ChromatogramConverterMSD.getInstance().getChromatogramConverterSupport();
	}

	@Override
	protected void tearDown() throws Exception {

		support = null;
		super.tearDown();
	}

	public void testFilterNames_1() {

		String[] filterNames = support.getFilterNames(IConverterSupport.ALL_SUPPLIER);
		String result = "";
		for(String name : filterNames) {
			result += name + ";";
		}
		/*
		 * There could be more converter. But these 3 should be there in
		 * every case.
		 */
		assertTrue("Amount Filter Names", 3 <= filterNames.length);
		assertEquals("FilterName", true, result.contains("OpenChrom Chromatogram (*.ocb)"));
	}
}
