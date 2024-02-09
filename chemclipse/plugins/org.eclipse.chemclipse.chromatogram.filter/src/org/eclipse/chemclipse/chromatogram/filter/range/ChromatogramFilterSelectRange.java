/*******************************************************************************
 * Copyright (c) 2020, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Matthias Mailänder - reimplemented
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.range;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.chemclipse.chromatogram.filter.impl.settings.FilterSettingsSelection;
import org.eclipse.chemclipse.chromatogram.filter.l10n.Messages;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.swtchart.extensions.menu.IChartMenuCategories;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class ChromatogramFilterSelectRange implements IProcessTypeSupplier {

	private static final String ID = "org.eclipse.chemclipse.chromatogram.filter.setChromatogramSelection";
	private static final String NAME = "Chromatogram Selection (Select Range)";
	private static final String DESCRIPTION = "This filter selects the chromatogram range.";

	@Override
	public String getCategory() {

		return IChartMenuCategories.RANGE_SELECTION;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new ProcessSupplier(this));
	}

	private static final class ProcessSupplier extends AbstractProcessSupplier<FilterSettingsSelection> implements IChromatogramSelectionProcessSupplier<FilterSettingsSelection> {

		public ProcessSupplier(IProcessTypeSupplier parent) {

			super(ID, NAME, DESCRIPTION, FilterSettingsSelection.class, parent, DataCategory.CSD, DataCategory.MSD, DataCategory.WSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, FilterSettingsSelection processSettings, ProcessExecutionContext context) throws InterruptedException {

			/*
			 * Settings
			 */
			double startRetentionTime = processSettings.getStartRetentionTimeMinutes() * IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
			double stopRetentionTime = processSettings.getStopRetentionTimeMinutes() * IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
			/*
			 * Start Retention Time
			 */
			if(Double.isInfinite(startRetentionTime)) {
				startRetentionTime = chromatogramSelection.getChromatogram().getStartRetentionTime() * Math.signum(startRetentionTime);
			} else if(processSettings.isStartRelative()) {
				startRetentionTime = chromatogramSelection.getStartRetentionTime() + startRetentionTime;
			}
			/*
			 * Stop Retention Time
			 */
			if(Double.isInfinite(stopRetentionTime)) {
				stopRetentionTime = chromatogramSelection.getChromatogram().getStopRetentionTime() * Math.signum(startRetentionTime);
			} else if(processSettings.isStopRelative()) {
				stopRetentionTime = chromatogramSelection.getStopRetentionTime() + stopRetentionTime;
			}
			/*
			 * Validations
			 * Start Retention Time: 0 is allowed. It is implicit a start from the beginning of the chromatogram.
			 */
			if(startRetentionTime >= chromatogramSelection.getChromatogram().getStopRetentionTime()) {
				context.addErrorMessage(Messages.selectRange, Messages.startRetentionTimeOutsideRange);
			}
			//
			if(stopRetentionTime <= chromatogramSelection.getChromatogram().getStartRetentionTime()) {
				context.addWarnMessage(Messages.selectRange, Messages.stopRetentionTimeOutsideRange);
			}
			//
			float startAbundance = processSettings.getStartAbundance();
			if(processSettings.isStartAbundanceRelative()) {
				startAbundance = chromatogramSelection.getChromatogram().getMaxSignal() * processSettings.getStartAbundance() / 100;
			}
			float stopAbundance = processSettings.getStopAbundance();
			if(processSettings.isStopAbundanceRelative()) {
				stopAbundance = chromatogramSelection.getChromatogram().getMaxSignal() * processSettings.getStopAbundance() / 100;
			}
			chromatogramSelection.setRanges((int)startRetentionTime, (int)stopRetentionTime, startAbundance, stopAbundance);
			return chromatogramSelection;
		}
	}
}
