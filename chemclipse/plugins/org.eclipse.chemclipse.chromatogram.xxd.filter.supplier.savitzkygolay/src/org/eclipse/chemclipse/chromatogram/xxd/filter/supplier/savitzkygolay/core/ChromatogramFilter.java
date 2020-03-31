/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.core;

import java.util.Iterator;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramSignalFilter;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.processor.SavitzkyGolayProcessor;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.MassSpectrumFilterSettings;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.matrix.ExtractedMatrix;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilter extends AbstractChromatogramSignalFilter {
	
	@Override
	public IChromatogramFilterResult process(IChromatogramSelectionMSD chromatogramSelection, IChromatogramFilterSettings filterSettings, IProgressMonitor monitor) {

		IChromatogramMSD chromatogramMSD = chromatogramSelection.getChromatogramMSD();
		ChromatogramFilterSettings settings = (ChromatogramFilterSettings) filterSettings;
		/*
		 * 1. step - export signal from chromatogram
		 */
		
		IChromatogramFilterResult chromatogramFilterResult;
		
		
		if(settings.getPerIonCalculation() == true) {
			ExtractedMatrix extractedMatrix = new ExtractedMatrix(chromatogramSelection);
			chromatogramFilterResult = filterProcess(extractedMatrix, filterSettings, monitor);
		} else {
			TotalScanSignalExtractor totalScanSignalExtractor = new TotalScanSignalExtractor(chromatogramMSD);
			
			/*
			 * here is different between CSD and MSD, in case msd is check if value are negative
			 */
			ITotalScanSignals totalSignals = totalScanSignalExtractor.getTotalScanSignals(chromatogramSelection, true);
			/*
			 * 2. step - process signal
			 */
			chromatogramFilterResult = filterProcess(totalSignals, filterSettings, monitor);
			/*
			 * here is different between CSD and MSD, in case msd is set negative value to zero
			 */
			totalSignals.setNegativeTotalSignalsToZero();
			/*
			 * 3. step - adjust signal in chromatogram if data has been process successfully
			 */
			Iterator<Integer> itScan = totalSignals.iterator();
			if(chromatogramFilterResult.getResultStatus().equals(ResultStatus.OK)) {
				while(itScan.hasNext()) {
					Integer scan = itScan.next();
					IScanMSD scanMSD = chromatogramMSD.getSupplierScan(scan);
					ITotalScanSignal totalscanSignal = totalSignals.getTotalScanSignal(scan);
					scanMSD.adjustTotalSignal(totalscanSignal.getTotalSignal());
				}
			}	
		}
		
		return chromatogramFilterResult;
	}

	@Override
	protected IChromatogramFilterResult applyFilter(ITotalScanSignals totalSignals, IChromatogramFilterSettings filterSettings, IProgressMonitor monitor) {

		SavitzkyGolayProcessor processor = new SavitzkyGolayProcessor();
		return processor.apply(totalSignals, (ChromatogramFilterSettings)filterSettings, monitor);
	}

	@Override
	protected IChromatogramFilterResult applyFilter(ITotalScanSignals totalSignals, IProgressMonitor monitor) {

		ChromatogramFilterSettings chromatogramFilterSettings = PreferenceSupplier.getFilterSettings();
		return applyFilter(totalSignals, chromatogramFilterSettings, monitor);
	}
	
	protected IChromatogramFilterResult applyFilter(ExtractedMatrix extractedMatrix, IChromatogramFilterSettings filterSettings, IProgressMonitor monitor) {

		SavitzkyGolayProcessor processor = new SavitzkyGolayProcessor();
		return processor.apply(extractedMatrix, (ChromatogramFilterSettings)filterSettings, monitor);
	}

	protected IChromatogramFilterResult applyFilter(ExtractedMatrix extractedMatrix, IProgressMonitor monitor) {

		ChromatogramFilterSettings chromatogramFilterSettings = PreferenceSupplier.getFilterSettings();
		return applyFilter(extractedMatrix, chromatogramFilterSettings, monitor);
	}
	 
	
	@Override
	public IChromatogramFilterResult filterProcess(ITotalScanSignals totalScanSignals, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		if(chromatogramFilterSettings == null) {
			return applyFilter(totalScanSignals, monitor);
		} else {
			return applyFilter(totalScanSignals, chromatogramFilterSettings, monitor);
		}
	}
	
	private IChromatogramFilterResult filterProcess(ExtractedMatrix extractedMatrix, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {
		if(chromatogramFilterSettings == null) {
			return applyFilter(extractedMatrix, monitor);
		} else {
			return applyFilter(extractedMatrix, chromatogramFilterSettings, monitor);
		}
	}
	
}
