/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.AbstractChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.exceptions.FilterException;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings.ChromatogramFilterSettings;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.support.util.IonSettingUtil;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilter extends AbstractChromatogramFilterMSD {

	@Override
	public IProcessingInfo<?> applyFilter(IChromatogramSelectionMSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo<Object> processingInfo = new ProcessingInfo<>();
		processingInfo.addMessages(validate(chromatogramSelection, chromatogramFilterSettings));
		//
		if(!processingInfo.hasErrorMessages()) {
			if(chromatogramFilterSettings instanceof ChromatogramFilterSettings) {
				try {
					ChromatogramFilterSettings filterSettings = (ChromatogramFilterSettings)chromatogramFilterSettings;
					IonSettingUtil ionSettingUtil = new IonSettingUtil();
					IMarkedIons ionsToRemove = new MarkedIons(ionSettingUtil.extractIons(ionSettingUtil.deserialize(filterSettings.getIonsToRemove())), IMarkedIons.IonMarkMode.INCLUDE);
					applyIonRemoverFilter(chromatogramSelection, ionsToRemove, monitor);
					processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "Mass fragments have been removed successfully."));
					chromatogramSelection.getChromatogram().setDirty(true);
				} catch(FilterException e) {
					processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.EXCEPTION, e.getMessage()));
				}
			}
		}
		return processingInfo;
	}

	// TODO JUnit
	@Override
	public IProcessingInfo<?> applyFilter(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		ChromatogramFilterSettings filterSettings = PreferenceSupplier.getFilterSettings();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}

	/**
	 * Removes the given ions stored in the excludedIons
	 * instance from the chromatogram selection.
	 * 
	 * @param chromatogramSelection
	 * @throws FilterException
	 */
	private void applyIonRemoverFilter(IChromatogramSelectionMSD chromatogramSelection, IMarkedIons ionsToRemove, IProgressMonitor monitor) throws FilterException {

		/*
		 * Test if there are ions to remove.
		 */
		if(ionsToRemove == null) {
			throw new FilterException("The excluded ions instance was null.");
		}
		if(ionsToRemove.getIonsNominal().size() == 0) {
			throw new FilterException("There was no ion stored to be excluded.");
		}
		IVendorMassSpectrum supplierMassSpectrum;
		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogram();
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		/*
		 * Iterate through all selected scans and remove the stored excluded
		 * ions.
		 */
		for(int scan = startScan; scan <= stopScan; scan++) {
			monitor.subTask("Remove ions from scan: " + scan);
			supplierMassSpectrum = chromatogram.getSupplierScan(scan);
			supplierMassSpectrum.removeIons(ionsToRemove);
		}
	}
}
