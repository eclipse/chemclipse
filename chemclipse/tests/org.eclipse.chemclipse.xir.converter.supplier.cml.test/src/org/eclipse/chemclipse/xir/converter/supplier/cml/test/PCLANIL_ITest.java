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
package org.eclipse.chemclipse.xir.converter.supplier.cml.test;

import java.io.File;

import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xir.converter.supplier.cml.PathResolver;
import org.eclipse.chemclipse.xir.converter.supplier.cml.converter.ScanImportConverter;
import org.eclipse.chemclipse.xir.converter.supplier.cml.model.IVendorSpectrumXIR;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import junit.framework.TestCase;

public class PCLANIL_ITest extends TestCase {

	private IVendorSpectrumXIR vendorSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		File file = new File(PathResolver.getAbsolutePath(TestPathHelper.PCLANIL));
		ScanImportConverter importConverter = new ScanImportConverter();
		IProcessingInfo<IVendorSpectrumXIR> processingInfo = importConverter.convert(file, new NullProgressMonitor());
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
		assertEquals("para-chloroaniline", vendorSpectrum.getSampleName());
		assertEquals("IR_para-chlor", vendorSpectrum.getDataName());
		assertEquals("C6H6ClN", vendorSpectrum.getFormula());
		assertEquals("106-47-8", vendorSpectrum.getCasNumber());
		assertEquals("PERKIN-ELMER 1000 FT-IR", vendorSpectrum.getInstrument());
	}

	@Test
	public void testSignals() {

		assertEquals(1801, vendorSpectrum.getScanISD().getProcessedSignals().size());
	}
}
