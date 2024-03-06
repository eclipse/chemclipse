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
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.processor.SavitzkyGolayProcessor;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ChromatogramFilterSettingsISD;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.vsd.model.core.IChromatogramVSD;
import org.eclipse.chemclipse.vsd.model.core.IScanVSD;
import org.eclipse.chemclipse.vsd.model.core.selection.IChromatogramSelectionVSD;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class ChromatogramFilterVSD implements IProcessTypeSupplier {

	private static final String ID = "org.eclipse.chemclipse.chromatogram.vsd.filter.supplier.savitzkygolay";
	private static final String NAME = "Savitzky-Golay Smoothing";
	private static final String DESCRIPTION = "Savitzky-Golay of the chromatogram data.";

	@Override
	public String getCategory() {

		return ICategories.CHROMATOGRAM_FILTER;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new ProcessSupplier(this));
	}

	private static final class ProcessSupplier extends AbstractProcessSupplier<ChromatogramFilterSettingsISD> implements IChromatogramSelectionProcessSupplier<ChromatogramFilterSettingsISD> {

		public ProcessSupplier(IProcessTypeSupplier parent) {

			super(ID, NAME, DESCRIPTION, ChromatogramFilterSettingsISD.class, parent, DataCategory.VSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, ChromatogramFilterSettingsISD processSettings, ProcessExecutionContext context) throws InterruptedException {

			if(chromatogramSelection instanceof IChromatogramSelectionVSD chromatogramSelectionVSD) {
				/*
				 * Process
				 */
				IChromatogramVSD chromatogramVSD = chromatogramSelectionVSD.getChromatogram();
				TotalScanSignalExtractor totalScanSignalExtractor = new TotalScanSignalExtractor(chromatogramVSD);
				ITotalScanSignals totalSignals = totalScanSignalExtractor.getTotalScanSignals(chromatogramSelection, false);
				SavitzkyGolayProcessor.apply(totalSignals, processSettings, new NullProgressMonitor());
				chromatogramVSD.setDirty(true);
				//
				Iterator<Integer> iteratorScans = totalSignals.iterator();
				while(iteratorScans.hasNext()) {
					int scanNumber = iteratorScans.next();
					IScan scan = chromatogramVSD.getScan(scanNumber);
					if(scan instanceof IScanVSD scanVSD) {
						ITotalScanSignal totalScanSignal = totalSignals.getTotalScanSignal(scanNumber);
						scanVSD.adjustTotalSignal(totalScanSignal.getTotalSignal());
					}
				}
			}
			//
			return chromatogramSelection;
		}
	}
}