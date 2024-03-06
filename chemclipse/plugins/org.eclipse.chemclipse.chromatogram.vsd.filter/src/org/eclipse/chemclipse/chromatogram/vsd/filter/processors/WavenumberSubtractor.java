/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.vsd.filter.processors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.vsd.filter.model.WavenumberSignal;
import org.eclipse.chemclipse.chromatogram.vsd.filter.model.WavenumberSignals;
import org.eclipse.chemclipse.chromatogram.vsd.filter.settings.WavenumberSubtractorSettings;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.vsd.model.core.IChromatogramVSD;
import org.eclipse.chemclipse.vsd.model.core.IScanVSD;
import org.eclipse.chemclipse.vsd.model.core.ISignalVSD;
import org.eclipse.chemclipse.vsd.model.core.SignalType;
import org.eclipse.chemclipse.vsd.model.core.selection.IChromatogramSelectionVSD;
import org.eclipse.chemclipse.vsd.model.implementation.SignalInfrared;
import org.eclipse.chemclipse.vsd.model.implementation.SignalRaman;
import org.eclipse.chemclipse.vsd.model.support.CombinedScanCalculator;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class WavenumberSubtractor implements IProcessTypeSupplier {

	private static final String ID = "org.eclipse.chemclipse.chromatogram.vsd.filter.processors.wavenumberSubtractor";
	private static final String NAME = "Wavenumber Subtractor";
	private static final String DESCRIPTION = "Cleans the spectra of an VSD chromatogram.";
	//
	private static final double NORMALIZED_INTENSITY = 100.0d;

	@Override
	public String getCategory() {

		return ICategories.SCAN_FILTER;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new ProcessSupplier(this));
	}

	private static final class ProcessSupplier extends AbstractProcessSupplier<WavenumberSubtractorSettings> implements IChromatogramSelectionProcessSupplier<WavenumberSubtractorSettings> {

		public ProcessSupplier(IProcessTypeSupplier parent) {

			super(ID, NAME, DESCRIPTION, WavenumberSubtractorSettings.class, parent, DataCategory.VSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, WavenumberSubtractorSettings processSettings, ProcessExecutionContext context) throws InterruptedException {

			if(chromatogramSelection instanceof IChromatogramSelectionVSD chromatogramSelectionISD) {
				/*
				 * Subtract Signals
				 */
				SignalType signalType = getSignalType(chromatogramSelectionISD);
				boolean nominalizeWavenumber = processSettings.isNominalizeWavenumber();
				boolean normalizeIntensity = processSettings.isNormalizeIntensity();
				List<ISignalVSD> subtractSignals = getSubtractSignals(processSettings.getWavenumberSignals(), signalType, nominalizeWavenumber);
				if(normalizeIntensity) {
					normalizeIntensities(subtractSignals);
				}
				//
				if(!subtractSignals.isEmpty()) {
					/*
					 * Settings
					 */
					IChromatogramVSD chromatogramISD = chromatogramSelectionISD.getChromatogram();
					int startScan = chromatogramISD.getScanNumber(chromatogramSelection.getStartRetentionTime());
					int stopScan = chromatogramISD.getScanNumber(chromatogramSelection.getStopRetentionTime());
					//
					for(int scan = startScan; scan <= stopScan; scan++) {
						IScan scanX = chromatogramISD.getScan(scan);
						if(scanX instanceof IScanVSD scanISD) {
							/*
							 * Normalize
							 */
							if(normalizeIntensity) {
								normalizeIntensities(new ArrayList<>(scanISD.getProcessedSignals()));
							}
							subtract(subtractSignals, scanISD, nominalizeWavenumber);
						}
					}
				}
			}
			return chromatogramSelection;
		}

		private SignalType getSignalType(IChromatogramSelectionVSD chromatogramSelectionISD) {

			SignalType signalType = SignalType.RAMAN;
			//
			IChromatogramVSD chromatogram = chromatogramSelectionISD.getChromatogram();
			List<IScan> scans = chromatogram.getScans();
			if(!scans.isEmpty()) {
				IScan scan = scans.get(0);
				if(scan instanceof IScanVSD scanISD) {
					signalType = scanISD.getSignalType();
				}
			}
			//
			return signalType;
		}

		private void normalizeIntensities(List<ISignalVSD> signals) {

			double minIntensity = signals.stream().mapToDouble(ISignalVSD::getIntensity).min().getAsDouble();
			double maxIntensity = signals.stream().mapToDouble(ISignalVSD::getIntensity).max().getAsDouble();
			/*
			 * Min
			 */
			if(minIntensity < 0) {
				double factorMin = -NORMALIZED_INTENSITY / minIntensity;
				for(ISignalVSD signal : signals) {
					double intensity = signal.getIntensity();
					if(intensity < 0) {
						signal.setIntensity(factorMin * intensity);
					}
				}
			}
			/*
			 * Max
			 */
			if(maxIntensity > 0) {
				double factorMax = NORMALIZED_INTENSITY / maxIntensity;
				for(ISignalVSD signal : signals) {
					double intensity = signal.getIntensity();
					if(intensity > 0) {
						signal.setIntensity(factorMax * intensity);
					}
				}
			}
		}

		private void subtract(List<ISignalVSD> signalsSubtract, IScanVSD scanISD, boolean nominalizeWavenumber) {

			/*
			 * Map the signals
			 */
			Map<Double, ISignalVSD> processedSignalsMap = new HashMap<>();
			for(ISignalVSD signal : scanISD.getProcessedSignals()) {
				double wavenumber = nominalizeWavenumber ? CombinedScanCalculator.getWavenumber(signal.getWavenumber()) : signal.getWavenumber();
				processedSignalsMap.put(wavenumber, signal);
			}
			/*
			 * Process
			 */
			Iterator<ISignalVSD> iteratorSignalsSubtract = signalsSubtract.iterator();
			while(iteratorSignalsSubtract.hasNext()) {
				ISignalVSD signal = iteratorSignalsSubtract.next();
				double wavenumber = signal.getWavenumber();
				ISignalVSD processedSignal = processedSignalsMap.get(wavenumber);
				if(processedSignal != null) {
					double intensity = processedSignal.getIntensity() - signal.getIntensity();
					processedSignal.setIntensity(intensity);
				}
			}
		}

		private List<ISignalVSD> getSubtractSignals(WavenumberSignals wavenumberSignals, SignalType signalType, boolean nominalizeWavenumber) {

			List<ISignalVSD> signals = new ArrayList<>();
			//
			for(WavenumberSignal wavenumberSignal : wavenumberSignals) {
				/*
				 * Optimize
				 */
				double wavenumber = wavenumberSignal.getWavenumber();
				double intensity = wavenumberSignal.getIntensity();
				if(nominalizeWavenumber) {
					wavenumber = CombinedScanCalculator.getWavenumber(wavenumber);
				}
				/*
				 * Create Signal
				 */
				ISignalVSD signal;
				switch(signalType) {
					case FTIR:
						signal = new SignalInfrared(wavenumber, intensity);
						break;
					default:
						signal = new SignalRaman(wavenumber, intensity);
						break;
				}
				signals.add(signal);
			}
			//
			return signals;
		}
	}
}