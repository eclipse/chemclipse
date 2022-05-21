/*******************************************************************************
 * Copyright (c) 2011, 2022 Lablicate GmbH.
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

public class AbstractConverterSupport_3_Test extends AbstractConverterTestCase {

	private IConverterSupportSetter converterSupport;
	private String[] filterExtensions;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		converterSupport = getConverterSupport();
		filterExtensions = converterSupport.getFilterExtensions(IConverterSupport.EXPORT_SUPPLIER);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetExportableFilterExtensions_1() {

		int size = filterExtensions.length;
		assertEquals(2, size);
	}

	public void testGetExportableFilterExtensions_2() {

		String extension = filterExtensions[0];
		assertEquals("*.CDF;*.cdf", extension);
	}

	public void testGetExportableFilterExtensions_3() {

		String extension = filterExtensions[1];
		assertEquals("*.xlsx;*.XLSX", extension);
	}
}