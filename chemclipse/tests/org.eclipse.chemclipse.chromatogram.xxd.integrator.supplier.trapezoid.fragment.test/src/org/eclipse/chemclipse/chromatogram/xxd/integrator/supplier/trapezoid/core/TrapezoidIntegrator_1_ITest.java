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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.core;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.combined.ICombinedIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.ICombinedIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.TestPathHelper;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.core.ChromatogramImportTestCase;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings.ChromatogramIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings.CombinedIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings.PeakIntegrationSettings;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.core.runtime.NullProgressMonitor;

public class TrapezoidIntegrator_1_ITest extends ChromatogramImportTestCase {

	private CombinedIntegrator integrator;
	private ICombinedIntegrationSettings combinedIntegrationSettings;
	private ChromatogramIntegrationSettings chromatogramIntegrationSettings;
	private PeakIntegrationSettings peakIntegrationSettings;

	@Override
	protected void setUp() throws Exception {

		chromatogramRelativePath = TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_1;
		super.setUp();
		integrator = new CombinedIntegrator();
		chromatogramIntegrationSettings = new ChromatogramIntegrationSettings();
		peakIntegrationSettings = new PeakIntegrationSettings();
		combinedIntegrationSettings = new CombinedIntegrationSettings(chromatogramIntegrationSettings, peakIntegrationSettings);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testIntegrate_1() {

		IProcessingInfo<ICombinedIntegrationResult> processingInfo;
		ICombinedIntegrationResult result;
		IChromatogramIntegrationResults results;
		IChromatogramIntegrationResult integrationResult;
		//
		processingInfo = integrator.integrate(chromatogramSelection, combinedIntegrationSettings, new NullProgressMonitor());
		try {
			result = processingInfo.getProcessingResult();
			results = result.getChromatogramIntegrationResults();
			integrationResult = results.getChromatogramIntegrationResult(0);
			assertEquals("Ion", AbstractIon.TIC_ION, integrationResult.getIon());
			assertEquals("BackgroundArea", 0.0d, integrationResult.getBackgroundArea());
			assertEquals("ChromatogramArea", 7.933076834045009E9, integrationResult.getChromatogramArea());
		} catch(TypeCastException e) {
			assertTrue("TypeCastException", false);
		}
	}
}
