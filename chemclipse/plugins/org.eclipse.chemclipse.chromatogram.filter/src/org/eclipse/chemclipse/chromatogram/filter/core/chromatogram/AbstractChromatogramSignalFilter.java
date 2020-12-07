/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.core.chromatogram;

import java.util.Iterator;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.xwc.ExtractedSingleWavelengthSignalExtractor;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedSingleWavelengthSignal;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedSingleWavelengthSignals;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("rawtypes")
public abstract class AbstractChromatogramSignalFilter extends AbstractChromatogramFilter implements IChromatogramFilter {

	protected abstract IChromatogramFilterResult applyFilter(ITotalScanSignals totalSignals, IChromatogramFilterSettings filterSettings, IProgressMonitor monitor);

	protected abstract IChromatogramFilterResult applyFilter(ITotalScanSignals totalSignals, IProgressMonitor monitor);

	@SuppressWarnings("unchecked")
	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			IChromatogramFilterResult chromatogramFilterResult = process(chromatogramSelection, chromatogramFilterSettings, monitor);
			processingInfo.setProcessingResult(chromatogramFilterResult);
		}
		return processingInfo;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = validateChromatogramSelection(chromatogramSelection);
		if(!processingInfo.hasErrorMessages()) {
			IChromatogramFilterResult chromatogramFilterResult = process(chromatogramSelection, null, monitor);
			processingInfo.setProcessingResult(chromatogramFilterResult);
		}
		return processingInfo;
	}

	private IChromatogramFilterResult process(IChromatogramSelection chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			return process((IChromatogramSelectionMSD)chromatogramSelection, chromatogramFilterSettings, monitor);
		} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
			return process((IChromatogramSelectionCSD)chromatogramSelection, chromatogramFilterSettings, monitor);
		} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
			return process((IChromatogramSelectionWSD)chromatogramSelection, chromatogramFilterSettings, monitor);
		}
		throw new UnsupportedOperationException("Class " + chromatogramSelection.getClass().getName() + " is not supported");
	}

	private IChromatogramFilterResult process(IChromatogramSelectionCSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IChromatogramCSD chromatogramCSD = chromatogramSelection.getChromatogram();
		/*
		 * 1. step - export signal from chromatogram
		 */
		TotalScanSignalExtractor totalScanSignalExtractor = new TotalScanSignalExtractor(chromatogramCSD);
		/*
		 * here is different between CSD and MSD, in case csd is NOT check if value are negative
		 */
		ITotalScanSignals totalSignals = totalScanSignalExtractor.getTotalScanSignals(chromatogramSelection, false);
		/*
		 * 2. step - process signal
		 */
		IChromatogramFilterResult chromatogramFilterResult = filterProcess(totalSignals, chromatogramFilterSettings, monitor);
		/*
		 * 3. step - adjust signal in chromatogram if data has been process successfully
		 */
		if(chromatogramFilterResult.getResultStatus().equals(ResultStatus.OK)) {
			Iterator<Integer> itScan = totalSignals.iterator();
			while(itScan.hasNext()) {
				Integer scan = itScan.next();
				IScanCSD scanCSD = chromatogramCSD.getSupplierScan(scan);
				ITotalScanSignal totalscanSignal = totalSignals.getTotalScanSignal(scan);
				scanCSD.adjustTotalSignal(totalscanSignal.getTotalSignal());
			}
		}
		return chromatogramFilterResult;
	}

	public IChromatogramFilterResult process(IChromatogramSelectionMSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IChromatogramMSD chromatogramMSD = chromatogramSelection.getChromatogram();
		/*
		 * 1. step - export signal from chromatogram
		 */
		TotalScanSignalExtractor totalScanSignalExtractor = new TotalScanSignalExtractor(chromatogramMSD);
		/*
		 * here is different between CSD and MSD, in case msd is check if value are negative
		 */
		ITotalScanSignals totalSignals = totalScanSignalExtractor.getTotalScanSignals(chromatogramSelection, true);
		/*
		 * 2. step - process signal
		 */
		IChromatogramFilterResult chromatogramFilterResult = filterProcess(totalSignals, chromatogramFilterSettings, monitor);
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
		return chromatogramFilterResult;
	}

	private IChromatogramFilterResult process(IChromatogramSelectionWSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IChromatogramWSD chromatogramWSD = chromatogramSelection.getChromatogram();
		/*
		 * 1. step - export signals from chromatogram
		 */
		ExtractedSingleWavelengthSignalExtractor extractor = new ExtractedSingleWavelengthSignalExtractor(chromatogramWSD, false);
		List<IExtractedSingleWavelengthSignals> extractedSingleWavelengthSignals = extractor.getExtractedWavelengthSignals(chromatogramSelection);
		IChromatogramFilterResult chromatogramFilterResultFinal = null;
		/*
		 * 2. step - process signals - each wavelength separately
		 */
		for(IExtractedSingleWavelengthSignals totalSignals : extractedSingleWavelengthSignals) {
			IChromatogramFilterResult chromatogramFilterResult = filterProcess(totalSignals, chromatogramFilterSettings, monitor);
			/*
			 * if any signal processing fail do not change chromatogram and return error
			 */
			if(!chromatogramFilterResult.getResultStatus().equals(ResultStatus.OK)) {
				return chromatogramFilterResult;
			}
			chromatogramFilterResultFinal = chromatogramFilterResult;
		}
		/*
		 * 3. step - adjust signal in chromatogram if data has been process successfully
		 */
		for(IExtractedSingleWavelengthSignals totalSignals : extractedSingleWavelengthSignals) {
			Iterator<Integer> itScan = totalSignals.iterator();
			double wavelength = totalSignals.getWavelength();
			while(itScan.hasNext()) {
				int scan = itScan.next();
				IScanSignalWSD scanSignal = chromatogramWSD.getSupplierScan(scan).getScanSignal(wavelength).get();
				IExtractedSingleWavelengthSignal totalSignal = totalSignals.getTotalScanSignal(scan);
				scanSignal.setAbundance(totalSignal.getTotalSignal());
			}
		}
		return chromatogramFilterResultFinal;
	}

	public IChromatogramFilterResult filterProcess(ITotalScanSignals totalScanSignals, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		if(chromatogramFilterSettings == null) {
			return applyFilter(totalScanSignals, monitor);
		} else {
			return applyFilter(totalScanSignals, chromatogramFilterSettings, monitor);
		}
	}
}
