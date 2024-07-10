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
package org.eclipse.chemclipse.xxd.filter.chromatogram;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.xxd.filter.chromatogram.settings.IntensityOption;
import org.eclipse.chemclipse.xxd.filter.chromatogram.settings.ShiftIntensityFilterSettings;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class ShiftIntensityFilter implements IProcessTypeSupplier {

	private static final String ID = "org.eclipse.chemclipse.xxd.filter.chromatogram.shiftIntensity";
	private static final String NAME = "Shift Intensity";
	private static final String DESCRIPTION = "Shift the intensity by a given value.";

	@Override
	public String getCategory() {

		return ICategories.CHROMATOGRAM_FILTER;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new ProcessSupplier(this));
	}

	private static final class ProcessSupplier extends AbstractProcessSupplier<ShiftIntensityFilterSettings> implements IChromatogramSelectionProcessSupplier<ShiftIntensityFilterSettings> {

		public ProcessSupplier(IProcessTypeSupplier parent) {

			super(ID, NAME, DESCRIPTION, ShiftIntensityFilterSettings.class, parent, DataCategory.CSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, ShiftIntensityFilterSettings processSettings, ProcessExecutionContext context) throws InterruptedException {

			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
			int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
			//
			float shiftIntensity;
			IntensityOption intensityOption = processSettings.getIntensityOption();
			switch(intensityOption) {
				case RELATIVE:
					float factor = processSettings.getShiftIntensity() / 100.0f;
					float maxSignal = chromatogram.getMaxSignal();
					shiftIntensity = maxSignal * factor;
					break;
				default:
					shiftIntensity = processSettings.getShiftIntensity();
					break;
			}
			//
			for(int i = startScan; i <= stopScan; i++) {
				IScan scan = chromatogram.getScan(i);
				float totalSignal = scan.getTotalSignal() + shiftIntensity;
				scan.adjustTotalSignal(totalSignal);
			}
			//
			return chromatogramSelection;
		}
	}
}