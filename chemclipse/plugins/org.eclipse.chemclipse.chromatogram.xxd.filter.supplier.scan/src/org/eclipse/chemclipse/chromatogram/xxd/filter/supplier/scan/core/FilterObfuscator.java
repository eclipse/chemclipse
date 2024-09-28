/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.core;

import java.util.Random;
import java.util.TreeSet;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.settings.FilterSettingsObfuscator;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.chemclipse.vsd.model.core.IScanVSD;
import org.eclipse.chemclipse.vsd.model.core.ISignalInfrared;
import org.eclipse.chemclipse.vsd.model.core.ISignalVSD;
import org.eclipse.chemclipse.vsd.model.implementation.SignalInfrared;
import org.eclipse.chemclipse.vsd.model.implementation.SignalRaman;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.implementation.ScanSignalWSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class FilterObfuscator extends AbstractChromatogramFilter {

	private static final String TITLE = "Scan Obfuscator";
	private static final String MESSAGE = "Scans/peaks have been obfuscated successfully.";

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramFilterResult> processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(chromatogramFilterSettings instanceof FilterSettingsObfuscator settings) {
				applyObfuscatorFilter(chromatogramSelection, settings);
				processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, TITLE, MESSAGE));
				processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, MESSAGE));
				chromatogramSelection.getChromatogram().setDirty(true);
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsObfuscator filterSettings = PreferenceSupplier.getObfuscatorFilterSettings();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}

	private void applyObfuscatorFilter(IChromatogramSelection<?, ?> chromatogramSelection, FilterSettingsObfuscator settings) {

		if(chromatogramSelection != null) {
			/*
			 * Settings
			 */
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			Random random = new Random(System.currentTimeMillis());
			/*
			 * Scans
			 */
			if(settings.isScans()) {
				for(IScan scan : chromatogram.getScans()) {
					obfuscate(scan, random);
				}
			}
			/*
			 * Peaks
			 */
			if(settings.isPeaks()) {
				for(IPeak peak : chromatogram.getPeaks()) {
					IPeakModel peakModel = peak.getPeakModel();
					obfuscate(peakModel.getPeakMaximum(), random);
				}
			}
		}
	}

	private void obfuscate(IScan scan, Random random) {

		/*
		 * CSD don't need to be obfuscated.
		 */
		float totalSignal = scan.getTotalSignal();
		if(scan instanceof IScanMSD scanMSD) {
			/*
			 * MSD
			 */
			if(!scanMSD.getIons().isEmpty()) {
				scanMSD.removeAllIons();
				scanMSD.addIon(new Ion(random.nextInt(18, 250), totalSignal));
			}
		} else if(scan instanceof IScanVSD scanVSD) {
			/*
			 * VSD
			 */
			TreeSet<ISignalVSD> signals = scanVSD.getProcessedSignals();
			if(signals.isEmpty()) {
				boolean infrared = signals.first() instanceof ISignalInfrared;
				signals.clear();
				int wavenumber = random.nextInt(660, 4000);
				if(infrared) {
					signals.add(new SignalInfrared(wavenumber, totalSignal));
				} else {
					signals.add(new SignalRaman(wavenumber, totalSignal));
				}
			}
		} else if(scan instanceof IScanWSD scanWSD) {
			/*
			 * WSD
			 */
			if(!scanWSD.getScanSignals().isEmpty()) {
				scanWSD.deleteScanSignals();
				scanWSD.addScanSignal(new ScanSignalWSD(random.nextInt(380, 750), totalSignal));
			}
		}
	}
}