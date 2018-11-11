/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.core;

import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.IChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings.ChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings.ChromatogramFilterSettings;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.support.util.IonSettingUtil;
import org.eclipse.core.runtime.NullProgressMonitor;

public class MFRemoverFilter_1_ITest extends ChromatogramImporterTestCase {

	private IChromatogramFilterMSD chromatogramFilter;
	private ChromatogramFilterSettings chromatogramFilterSettings;
	private IMarkedIons excludedIons;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogramFilter = new ChromatogramFilter();
		chromatogramFilterSettings = new ChromatogramFilterSettings();
		IonSettingUtil settingIon = new IonSettingUtil();
		excludedIons = new MarkedIons(settingIon.extractIons(settingIon.deserialize(chromatogramFilterSettings.getIonsToRemove())));
		excludedIons.add(new MarkedIon(28));
		excludedIons.add(new MarkedIon(32));
		excludedIons.add(new MarkedIon(207));
		excludedIons.add(new MarkedIon(281));
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogramFilter = null;
		chromatogramFilterSettings = null;
		super.tearDown();
	}

	public void testApplyFilter_1() {

		int scan = 1;
		float sumExcludedIons = 2294055.0f;
		float result;
		result = chromatogram.getScan(scan).getTotalSignal() - sumExcludedIons;
		chromatogramFilter.applyFilter(chromatogramSelection, chromatogramFilterSettings, new NullProgressMonitor());
		assertEquals("total signal", result, chromatogram.getScan(scan).getTotalSignal());
	}

	public void testApplyFilter_2() {

		int scan = 300;
		float sumExcludedIons = 610039.0f;
		float result;
		result = chromatogram.getScan(scan).getTotalSignal() - sumExcludedIons;
		chromatogramFilter.applyFilter(chromatogramSelection, chromatogramFilterSettings, new NullProgressMonitor());
		assertEquals("total signal", result, chromatogram.getScan(scan).getTotalSignal());
	}

	public void testApplyFilter_3() {

		int scan = 5726;
		float sumExcludedIons = 40394.0f;
		float result;
		result = chromatogram.getScan(scan).getTotalSignal() - sumExcludedIons;
		chromatogramFilter.applyFilter(chromatogramSelection, chromatogramFilterSettings, new NullProgressMonitor());
		assertEquals("total signal", result, chromatogram.getScan(scan).getTotalSignal());
	}
}
