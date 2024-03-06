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
import java.util.Iterator;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.vsd.filter.settings.WavenumberCalibratorSettings;
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
import org.eclipse.chemclipse.vsd.model.core.IChromatogramVSD;
import org.eclipse.chemclipse.vsd.model.core.IScanVSD;
import org.eclipse.chemclipse.vsd.model.core.ISignalVSD;
import org.eclipse.chemclipse.vsd.model.core.selection.IChromatogramSelectionVSD;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class WavenumberCalibrator implements IProcessTypeSupplier {

	private static final String ID = "org.eclipse.chemclipse.chromatogram.vsd.filter.processors.wavenumberCalibrator";
	private static final String NAME = "Wavenumber Calibrator";
	private static final String DESCRIPTION = "Calibrates wavenumbers of a VSD chromatogram.";

	@Override
	public String getCategory() {

		return ICategories.SCAN_FILTER;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new ProcessSupplier(this));
	}

	private static final class ProcessSupplier extends AbstractProcessSupplier<WavenumberCalibratorSettings> implements IChromatogramSelectionProcessSupplier<WavenumberCalibratorSettings> {

		public ProcessSupplier(IProcessTypeSupplier parent) {

			super(ID, NAME, DESCRIPTION, WavenumberCalibratorSettings.class, parent, DataCategory.VSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, WavenumberCalibratorSettings processSettings, ProcessExecutionContext context) throws InterruptedException {

			if(chromatogramSelection instanceof IChromatogramSelectionVSD chromatogramSelectionISD) {
				List<Double> wavenumbers = getWavenumbers(processSettings.getWavenumbers());
				if(!wavenumbers.isEmpty()) {
					/*
					 * Settings
					 */
					int sizeWavenumbers = wavenumbers.size();
					IChromatogramVSD chromatogramISD = chromatogramSelectionISD.getChromatogram();
					int startScan = chromatogramISD.getScanNumber(chromatogramSelection.getStartRetentionTime());
					int stopScan = chromatogramISD.getScanNumber(chromatogramSelection.getStopRetentionTime());
					//
					for(int scan = startScan; scan <= stopScan; scan++) {
						IScan scanX = chromatogramISD.getScan(scan);
						if(scanX instanceof IScanVSD scanISD) {
							List<ISignalVSD> signals = new ArrayList<>(scanISD.getProcessedSignals());
							Collections.sort(signals, (s1, s2) -> Double.compare(s1.getWavenumber(), s2.getWavenumber()));
							int sizeSignals = signals.size();
							if(sizeWavenumbers >= sizeSignals) {
								Iterator<ISignalVSD> iteratorSignals = signals.iterator();
								Iterator<Double> iteratorWavenumbers = wavenumbers.iterator();
								while(iteratorSignals.hasNext()) {
									iteratorSignals.next().setWavenumber(iteratorWavenumbers.next());
								}
							}
						}
					}
				}
			}
			return chromatogramSelection;
		}

		private List<Double> getWavenumbers(String selection) {

			List<Double> wavelengths = new ArrayList<>();
			//
			String lineDelimiter = OperatingSystemUtils.getLineDelimiter();
			String delimiter = selection.contains(lineDelimiter) ? lineDelimiter : "\n";
			String[] lines = selection.split(delimiter);
			for(String line : lines) {
				String[] values = line.trim().split(" ");
				for(String value : values) {
					try {
						double wavelength = Double.parseDouble(value.trim());
						wavelengths.add(wavelength);
					} catch(NumberFormatException e) {
					}
				}
			}
			//
			return wavelengths;
		}
	}
}
