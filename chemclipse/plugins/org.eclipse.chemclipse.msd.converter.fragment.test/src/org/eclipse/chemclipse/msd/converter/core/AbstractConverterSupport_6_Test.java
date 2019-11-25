/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
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

import java.util.List;

import org.eclipse.chemclipse.converter.core.IConverterSupportSetter;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.processing.converter.ISupplier;

public class AbstractConverterSupport_6_Test extends AbstractConverterTestCase {

	private IConverterSupportSetter converterSupport;
	private List<ISupplier> supplier;
	private ISupplier actSupplier;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		converterSupport = getConverterSupport();
		supplier = converterSupport.getSupplier();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetSupplier_1() {

		assertEquals(5, supplier.size());
	}

	public void testGetSupplier_2() {

		String id = "org.eclipse.chemclipse.msd.converter.supplier.agilent";
		String filterName = "Agilent Chromatogram (*.D/DATA.MS)";
		try {
			actSupplier = converterSupport.getSupplier(id);
		} catch(NoConverterAvailableException e) {
			assertTrue("The failure NoConverterAvailableException should not be thrown here.", false);
		}
		assertEquals(filterName, actSupplier.getFilterName());
	}

	public void testGetSupplier_3() {

		String id = "org.eclipse.chemclipse.msd.converter.supplier.agilent.msd1";
		String filterName = "Agilent Chromatogram (*.D/MSD1.MS)";
		try {
			actSupplier = converterSupport.getSupplier(id);
		} catch(NoConverterAvailableException e) {
			assertTrue("The failure NoConverterAvailableException should not be thrown here.", false);
		}
		assertEquals(filterName, actSupplier.getFilterName());
	}

	public void testGetSupplier_4() {

		String id = "net.openchrom.msd.converter.supplier.cdf";
		String filterName = "ANDI/AIA CDF Chromatogram (*.CDF)";
		try {
			actSupplier = converterSupport.getSupplier(id);
		} catch(NoConverterAvailableException e) {
			assertTrue("The failure NoConverterAvailableException should not be thrown here.", false);
		}
		assertEquals(filterName, actSupplier.getFilterName());
	}

	public void testGetSupplier_5() {

		String id = "org.eclipse.chemclipse.msd.converter.supplier.excel";
		String filterName = "Excel Chromatogram (*.xlsx)";
		try {
			actSupplier = converterSupport.getSupplier(id);
		} catch(NoConverterAvailableException e) {
			assertTrue("The failure NoConverterAvailableException should not be thrown here.", false);
		}
		assertEquals(filterName, actSupplier.getFilterName());
	}

	public void testGetSupplier_6() {

		String id = "org.eclipse.chemclipse.msd.converter.supplier.test";
		String filterName = "Test Chromatogram (*.C/CHROM.MS)";
		try {
			actSupplier = converterSupport.getSupplier(id);
		} catch(NoConverterAvailableException e) {
			assertTrue("The failure NoConverterAvailableException should not be thrown here.", false);
		}
		assertEquals(filterName, actSupplier.getFilterName());
	}
}
