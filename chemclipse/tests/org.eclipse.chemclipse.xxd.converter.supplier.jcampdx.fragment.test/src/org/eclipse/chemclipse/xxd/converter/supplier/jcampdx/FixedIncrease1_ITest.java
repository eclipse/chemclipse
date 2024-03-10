/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.jcampdx;

import java.io.File;

import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.vsd.converter.supplier.jcampdx.converter.ScanImportConverter;
import org.eclipse.chemclipse.vsd.converter.supplier.jcampdx.model.IVendorSpectrumVSD;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import junit.framework.TestCase;

public class FixedIncrease1_ITest extends TestCase {

	private IVendorSpectrumVSD vendorSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		File file = new File(PathResolver.getAbsolutePath(TestPathHelper.FIXINC1));
		ScanImportConverter importConverter = new ScanImportConverter();
		IProcessingInfo<IVendorSpectrumVSD> processingInfo = importConverter.convert(file, new NullProgressMonitor());
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
	public void testSignals() {

		assertEquals(3736, vendorSpectrum.getScanVSD().getProcessedSignals().size());
	}
}