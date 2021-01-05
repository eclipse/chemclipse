/*******************************************************************************
 * Copyright (c) 2011, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.core;

import org.eclipse.chemclipse.converter.core.IConverterSupport;
import org.eclipse.chemclipse.converter.core.IConverterSupportSetter;

public class AbstractConverterSupport_4_Test extends AbstractConverterTestCase {

	private IConverterSupportSetter converterSupport;
	private String[] filterNames;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		converterSupport = getConverterSupport();
		filterNames = converterSupport.getFilterNames(IConverterSupport.EXPORT_SUPPLIER);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetExportableFilterNames_1() {

		int size = filterNames.length;
		assertEquals(3, size);
	}

	public void testGetExportableFilterNames_2() {

		String extension = filterNames[0];
		assertEquals("ANDI/AIA CDF Chromatogram (*.CDF)", extension);
	}

	public void testGetExportableFilterNames_3() {

		String extension = filterNames[1];
		assertEquals("Excel Chromatogram (*.xlsx)", extension);
	}

	public void testGetExportableFilterNames_4() {

		String extension = filterNames[2];
		assertEquals("Test Chromatogram (*.C/CHROM.MS)", extension);
	}
}
