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
package org.eclipse.chemclipse.chromatogram.isd.filter.processors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.isd.filter.settings.WavenumberSubtractorSettings;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.chemclipse.xir.model.core.IChromatogramISD;
import org.eclipse.chemclipse.xir.model.core.IScanISD;
import org.eclipse.chemclipse.xir.model.core.ISignalXIR;
import org.eclipse.chemclipse.xir.model.core.SignalType;
import org.eclipse.chemclipse.xir.model.core.selection.IChromatogramSelectionISD;
import org.eclipse.chemclipse.xir.model.implementation.SignalInfrared;
import org.eclipse.chemclipse.xir.model.implementation.SignalRaman;
import org.eclipse.chemclipse.xir.model.support.CombinedScanCalculator;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class WavenumberSubtractor implements IProcessTypeSupplier {

	private static final String ID = "org.eclipse.chemclipse.chromatogram.isd.filter.processors.wavenumberSubtractor";
	private static final String NAME = "Wavenumber Subtractor";
	private static final String DESCRIPTION = "Cleans the spectra of an ISD chromatogram.";
	//
	private static final String LINE_DELIMITER = "\n";
	private static final String VALUE_DELIMITER = "\t";
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

			super(ID, NAME, DESCRIPTION, WavenumberSubtractorSettings.class, parent, DataCategory.ISD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, WavenumberSubtractorSettings processSettings, ProcessExecutionContext context) throws InterruptedException {

			if(chromatogramSelection instanceof IChromatogramSelectionISD chromatogramSelectionISD) {
				/*
				 * Subtract Signals
				 */
				SignalType signalType = getSignalType(chromatogramSelectionISD);
				boolean nominalizeWavenumber = processSettings.isNominalizeWavenumber();
				boolean normalizeIntensity = processSettings.isNormalizeIntensity();
				List<ISignalXIR> subtractSignals = getSubtractSignals(processSettings.getSubtractSignals(), signalType, nominalizeWavenumber);
				if(normalizeIntensity) {
					normalizeIntensities(subtractSignals);
				}
				//
				if(!subtractSignals.isEmpty()) {
					/*
					 * Settings
					 */
					IChromatogramISD chromatogramISD = chromatogramSelectionISD.getChromatogram();
					int startScan = chromatogramISD.getScanNumber(chromatogramSelection.getStartRetentionTime());
					int stopScan = chromatogramISD.getScanNumber(chromatogramSelection.getStopRetentionTime());
					//
					for(int scan = startScan; scan <= stopScan; scan++) {
						IScan scanX = chromatogramISD.getScan(scan);
						if(scanX instanceof IScanISD scanISD) {
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

		private SignalType getSignalType(IChromatogramSelectionISD chromatogramSelectionISD) {

			SignalType signalType = SignalType.RAMAN;
			//
			IChromatogramISD chromatogram = chromatogramSelectionISD.getChromatogram();
			List<IScan> scans = chromatogram.getScans();
			if(!scans.isEmpty()) {
				IScan scan = scans.get(0);
				if(scan instanceof IScanISD scanISD) {
					signalType = scanISD.getSignalType();
				}
			}
			//
			return signalType;
		}

		private void normalizeIntensities(List<ISignalXIR> signals) {

			double minIntensity = signals.stream().mapToDouble(ISignalXIR::getIntensity).min().getAsDouble();
			double maxIntensity = signals.stream().mapToDouble(ISignalXIR::getIntensity).max().getAsDouble();
			/*
			 * Min
			 */
			if(minIntensity < 0) {
				double factorMin = -NORMALIZED_INTENSITY / minIntensity;
				for(ISignalXIR signal : signals) {
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
				for(ISignalXIR signal : signals) {
					double intensity = signal.getIntensity();
					if(intensity > 0) {
						signal.setIntensity(factorMax * intensity);
					}
				}
			}
		}

		private void subtract(List<ISignalXIR> signalsSubtract, IScanISD scanISD, boolean nominalizeWavenumber) {

			/*
			 * Map the signals
			 */
			Map<Double, ISignalXIR> processedSignalsMap = new HashMap<>();
			for(ISignalXIR signalXIR : scanISD.getProcessedSignals()) {
				double wavenumber = nominalizeWavenumber ? CombinedScanCalculator.getWavenumber(signalXIR.getWavenumber()) : signalXIR.getWavenumber();
				processedSignalsMap.put(wavenumber, signalXIR);
			}
			/*
			 * Process
			 */
			Iterator<ISignalXIR> iteratorSignalsSubtract = signalsSubtract.iterator();
			while(iteratorSignalsSubtract.hasNext()) {
				ISignalXIR signalXIR = iteratorSignalsSubtract.next();
				double wavenumber = signalXIR.getWavenumber();
				ISignalXIR signal = processedSignalsMap.get(wavenumber);
				if(signal != null) {
					double intensity = signal.getIntensity() - signalXIR.getIntensity();
					signal.setIntensity(intensity);
				}
			}
		}

		private List<ISignalXIR> getSubtractSignals(String selection, SignalType signalType, boolean nominalizeWavenumber) {

			List<ISignalXIR> signals = new ArrayList<>();
			//
			String lineDelimiter = OperatingSystemUtils.getLineDelimiter();
			String delimiter = selection.contains(lineDelimiter) ? lineDelimiter : LINE_DELIMITER;
			String[] lines = selection.split(delimiter);
			for(String line : lines) {
				String[] values = line.trim().split(" ");
				for(String value : values) {
					String[] parts = value.trim().split(VALUE_DELIMITER);
					if(parts.length == 2) {
						try {
							/*
							 * Extract
							 */
							double wavenumber = Double.parseDouble(parts[0].trim());
							double intensity = Double.parseDouble(parts[1].trim());
							if(nominalizeWavenumber) {
								wavenumber = CombinedScanCalculator.getWavenumber(wavenumber);
							}
							/*
							 * Create Signal
							 */
							ISignalXIR signal;
							switch(signalType) {
								case FTIR:
									signal = new SignalInfrared(wavenumber, intensity);
									break;
								default:
									signal = new SignalRaman(wavenumber, intensity);
									break;
							}
							signals.add(signal);
						} catch(NumberFormatException e) {
						}
					}
				}
			}
			//
			return signals;
		}
	}
}