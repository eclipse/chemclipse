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
package org.eclipse.chemclipse.chromatogram.wsd.filter.processors;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.wsd.filter.settings.WaveSpectrumSignalRemoverSettings;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.MarkedTraceModus;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.implementation.ScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedWavelengthSignal;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class WaveSpectrumSignalRemover implements IProcessTypeSupplier {

	private static final String ID = "org.eclipse.chemclipse.chromatogram.wsd.filter.processors.waveSpectrumSignalRemover";
	private static final String NAME = "Wavelength Remover Filter";
	private static final String DESCRIPTION = "Removes wavelengths from a WSD chromatogram.";

	@Override
	public String getCategory() {

		return ICategories.SCAN_FILTER;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new ProcessSupplier(this));
	}

	private static final class ProcessSupplier extends AbstractProcessSupplier<WaveSpectrumSignalRemoverSettings> implements IChromatogramSelectionProcessSupplier<WaveSpectrumSignalRemoverSettings> {

		public ProcessSupplier(IProcessTypeSupplier parent) {

			super(ID, NAME, DESCRIPTION, WaveSpectrumSignalRemoverSettings.class, parent, DataCategory.WSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, WaveSpectrumSignalRemoverSettings processSettings, ProcessExecutionContext context) throws InterruptedException {

			if(chromatogramSelection instanceof IChromatogramSelectionWSD chromatogramSelectionWSD) {
				Set<Integer> wavelengths = getWavelengthsToRemove(processSettings.getWavelengthsToRemove());
				if(!wavelengths.isEmpty()) {
					/*
					 * Settings
					 */
					MarkedTraceModus markedTraceModus = processSettings.getMarkMode();
					IChromatogramWSD chromatogramWSD = chromatogramSelectionWSD.getChromatogram();
					int startScan = chromatogramWSD.getScanNumber(chromatogramSelection.getStartRetentionTime());
					int stopScan = chromatogramWSD.getScanNumber(chromatogramSelection.getStopRetentionTime());
					//
					for(int scan = startScan; scan <= stopScan; scan++) {
						IScan scanX = chromatogramWSD.getScan(scan);
						if(scanX instanceof IScanWSD scanWSD) {
							/*
							 * Signals
							 */
							if(MarkedTraceModus.INCLUDE.equals(markedTraceModus)) {
								scanWSD.removeScanSignals(wavelengths);
							} else {
								/*
								 * Collect
								 */
								Map<Integer, Float> extractedWavelengthMap = new HashMap<>();
								IExtractedWavelengthSignal extractedWavelengthSignal = scanWSD.getExtractedWavelengthSignal();
								for(int wavelength : wavelengths) {
									float abundance = extractedWavelengthSignal.getAbundance(wavelength);
									extractedWavelengthMap.put(wavelength, abundance);
								}
								/*
								 * Map
								 */
								scanWSD.deleteScanSignals();
								for(Map.Entry<Integer, Float> entry : extractedWavelengthMap.entrySet()) {
									scanWSD.addScanSignal(new ScanSignalWSD(entry.getKey(), entry.getValue()));
								}
							}
						}
					}
				}
			}
			return chromatogramSelection;
		}

		private Set<Integer> getWavelengthsToRemove(String selection) {

			Set<Integer> wavelengths = new HashSet<>();
			//
			String[] values = selection.split(" ");
			for(String value : values) {
				try {
					int wavelength = Integer.parseInt(value.trim());
					wavelengths.add(wavelength);
				} catch(NumberFormatException e) {
				}
			}
			//
			return wavelengths;
		}
	}
}