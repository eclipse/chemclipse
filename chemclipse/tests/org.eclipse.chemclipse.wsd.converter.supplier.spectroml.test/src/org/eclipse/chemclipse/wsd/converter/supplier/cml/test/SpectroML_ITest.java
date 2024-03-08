/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.supplier.cml.test;

import java.io.File;
import java.time.ZoneId;

import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.wsd.converter.supplier.spectroml.PathResolver;
import org.eclipse.chemclipse.wsd.converter.supplier.spectroml.converter.ScanImportConverter;
import org.eclipse.chemclipse.wsd.converter.supplier.spectroml.model.IVendorSpectrumWSD;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import junit.framework.TestCase;

public class SpectroML_ITest extends TestCase {

	private IVendorSpectrumWSD vendorSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		File file = new File(PathResolver.getAbsolutePath(TestPathHelper.SAMPLE));
		ScanImportConverter importConverter = new ScanImportConverter();
		IProcessingInfo<IVendorSpectrumWSD> processingInfo = importConverter.convert(file, new NullProgressMonitor());
		vendorSpectrum = processingInfo.getProcessingResult();
	}

	@Override
	protected void tearDown() throws Exception {

		vendorSpectrum = null;
		super.tearDown();
	}

	@Test
	public void testLoading() {

		assertNotNull(vendorSpectrum);
	}

	@Test
	public void testMetadata() {

		assertEquals("sample experiment", vendorSpectrum.getDataName());
		assertEquals("simple measurement of drinking water", vendorSpectrum.getDetailedInfo());
		assertEquals("HP 8453", vendorSpectrum.getInstrument());
		assertEquals(2000, vendorSpectrum.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().getYear());
		assertEquals("Paul DeRose", vendorSpectrum.getOperator());
		assertEquals("1063546374", vendorSpectrum.getBarcode());
		assertEquals("water", vendorSpectrum.getSampleName());
		assertEquals("7732-18-5", vendorSpectrum.getCasNumber());
		assertEquals("H2O", vendorSpectrum.getFormula());
	}

	@Test
	public void testSignals() {

		assertEquals(3, vendorSpectrum.getSignals().size());
	}
}