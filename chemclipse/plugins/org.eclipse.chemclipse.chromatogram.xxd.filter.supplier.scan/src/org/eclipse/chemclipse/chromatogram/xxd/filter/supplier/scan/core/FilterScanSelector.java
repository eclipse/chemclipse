/*******************************************************************************
 * Copyright (c) 2022, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Matthias Mailänder - reimplemented
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.core;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.exceptions.FilterException;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.settings.FilterSettingsScanSelector;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.model.support.RetentionIndexMap;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.swtchart.extensions.menu.IChartMenuCategories;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class FilterScanSelector implements IProcessTypeSupplier {

	private static final String ID = "org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scanselector";
	private static final String NAME = "Scan Selector";
	private static final String DESCRIPTION = "This filters selects a scan in the chromatogram.";

	@Override
	public String getCategory() {

		return IChartMenuCategories.RANGE_SELECTION;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new ProcessSupplier(this));
	}

	private static final class ProcessSupplier extends AbstractProcessSupplier<FilterSettingsScanSelector> implements IChromatogramSelectionProcessSupplier<FilterSettingsScanSelector> {

		public ProcessSupplier(IProcessTypeSupplier parent) {

			super(ID, NAME, DESCRIPTION, FilterSettingsScanSelector.class, parent, DataCategory.CSD, DataCategory.MSD, DataCategory.WSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, FilterSettingsScanSelector processSettings, ProcessExecutionContext context) throws InterruptedException {

			try {
				selectScan(chromatogramSelection, processSettings);
				context.addInfoMessage("Select Scan", "The scan has been selected successfully.");
				chromatogramSelection.getChromatogram().setDirty(true);
			} catch(FilterException e) {
				context.addWarnMessage("Select Scan", e.getMessage());
			}
			return chromatogramSelection;
		}

		private void selectScan(IChromatogramSelection<?, ?> chromatogramSelection, FilterSettingsScanSelector filterSettingsScanSelector) throws FilterException {

			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
			int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
			int scanNumber = getScanNumber(chromatogram, filterSettingsScanSelector);
			//
			if(scanNumber >= startScan && scanNumber <= stopScan) {
				IScan scan = chromatogram.getScan(scanNumber);
				chromatogramSelection.setSelectedScan(scan);
			} else {
				throw new FilterException("The scan is outside of the chromatogram selection range.");
			}
		}

		private int getScanNumber(IChromatogram<?> chromatogram, FilterSettingsScanSelector filterSettingsScanSelector) {

			int scanNumber;
			double value = filterSettingsScanSelector.getScanSelectorValue();
			switch(filterSettingsScanSelector.getScanSelectorOption()) {
				case SCAN_NUMER:
					scanNumber = (int)Math.round(value);
					break;
				case RETENTION_TIME_MS:
					scanNumber = chromatogram.getScanNumber((int)Math.round(value));
					break;
				case RETENTION_TIME_MIN:
					scanNumber = chromatogram.getScanNumber((int)Math.round(value * IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
					break;
				case RETENTION_INDEX:
					RetentionIndexMap retentionIndexMap = new RetentionIndexMap(chromatogram);
					int retentionTime = retentionIndexMap.getRetentionTime((int)Math.round(value));
					if(retentionTime > -1) {
						scanNumber = chromatogram.getScanNumber(retentionTime);
					} else {
						scanNumber = -1;
					}
					break;
				default:
					scanNumber = -1;
					break;
			}
			//
			return scanNumber;
		}
	}
}
