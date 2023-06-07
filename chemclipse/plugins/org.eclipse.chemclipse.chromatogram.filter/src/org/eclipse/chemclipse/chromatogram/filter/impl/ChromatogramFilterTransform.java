/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.impl;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.impl.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.filter.impl.settings.FilterSettingsTransform;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.csd.model.implementation.ChromatogramCSD;
import org.eclipse.chemclipse.csd.model.implementation.ScanCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilterTransform extends AbstractChromatogramFilter implements IChromatogramFilter {

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramFilterResult> processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(chromatogramFilterSettings instanceof FilterSettingsTransform filterSettings) {
				IChromatogram<? extends IPeak> chromatogram = chromatogramSelection.getChromatogram();
				IChromatogram<? extends IPeak> chromatogramReference = null;
				//
				if(chromatogram instanceof IChromatogramCSD chromatogramCSD) {
					int mz = filterSettings.getMz();
					chromatogramReference = convertToMSD(chromatogramCSD, mz);
				} else if(chromatogram instanceof IChromatogramMSD chromatogramMSD) {
					chromatogramReference = convertToCSD(chromatogramMSD);
				}
				/*
				 * Add as a reference
				 */
				if(chromatogramReference != null) {
					chromatogram.addReferencedChromatogram(chromatogramReference);
				}
			}
		}
		//
		return processingInfo;
	}

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsTransform filterSettings = PreferenceSupplier.getFilterSettingsTransform();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}

	private IChromatogramMSD convertToMSD(IChromatogramCSD chromatogramCSD, int mz) {

		IChromatogramMSD chromatogramMSD = new ChromatogramMSD();
		chromatogramMSD.setFile(chromatogramCSD.getFile());
		chromatogramMSD.setConverterId("");
		chromatogramMSD.setShortInfo("Converted from CSD to MSD");
		chromatogramMSD.setScanDelay(chromatogramCSD.getScanDelay());
		chromatogramMSD.setScanInterval(chromatogramCSD.getScanInterval());
		/*
		 * Scans
		 */
		for(IScan scan : chromatogramCSD.getScans()) {
			try {
				IScanMSD scanMSD = new VendorMassSpectrum();
				scanMSD.setRetentionTime(scan.getRetentionTime());
				scanMSD.setRetentionIndex(scan.getRetentionIndex());
				scanMSD.addIon(new Ion(mz, scan.getTotalSignal()));
				chromatogramMSD.addScan(scanMSD);
			} catch(Exception e) {
				//
			}
		}
		//
		return chromatogramMSD;
	}

	private IChromatogramCSD convertToCSD(IChromatogramMSD chromatogramMSD) {

		IChromatogramCSD chromatogramCSD = new ChromatogramCSD();
		chromatogramCSD.setFile(chromatogramMSD.getFile());
		chromatogramCSD.setConverterId("");
		chromatogramCSD.setShortInfo("Converted from MSD to CSD");
		chromatogramCSD.setScanDelay(chromatogramMSD.getScanDelay());
		chromatogramCSD.setScanInterval(chromatogramMSD.getScanInterval());
		/*
		 * Scans
		 */
		for(IScan scan : chromatogramMSD.getScans()) {
			IScanCSD scanCSD = new ScanCSD(scan.getTotalSignal());
			scanCSD.setRetentionTime(scan.getRetentionTime());
			scanCSD.setRetentionIndex(scan.getRetentionIndex());
			chromatogramCSD.addScan(scanCSD);
		}
		//
		return chromatogramCSD;
	}
}