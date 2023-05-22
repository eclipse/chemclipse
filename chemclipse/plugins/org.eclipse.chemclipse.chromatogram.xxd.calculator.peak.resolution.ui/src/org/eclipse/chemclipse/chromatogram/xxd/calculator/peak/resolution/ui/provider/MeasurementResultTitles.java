/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.peak.resolution.ui.provider;

import static org.eclipse.chemclipse.support.ui.swt.columns.ColumnBuilder.defaultSortableColumn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.peak.resolution.ui.l10n.Messages;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IPeakResolution;
import org.eclipse.chemclipse.support.ui.swt.columns.ColumnDefinition;
import org.eclipse.chemclipse.support.ui.swt.columns.ColumnDefinitionProvider;
import org.eclipse.osgi.util.NLS;

public class MeasurementResultTitles implements ColumnDefinitionProvider {

	@Override
	public Collection<? extends ColumnDefinition<?, ?>> getColumnDefinitions() {

		List<ColumnDefinition<?, ?>> list = new ArrayList<>();
		list.add(defaultSortableColumn(NLS.bind(Messages.peakRetentionTimeMinutes, 1), 150, new Function<IPeakResolution, Double>() {

			@Override
			public Double apply(IPeakResolution resolution) {

				return resolution.getPeak1().getPeakModel().getRetentionTimeAtPeakMaximum() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
			}
		}).create());
		list.add(defaultSortableColumn(NLS.bind(Messages.peakRetentionTimeMinutes, 2), 150, new Function<IPeakResolution, Double>() {

			@Override
			public Double apply(IPeakResolution resolution) {

				return resolution.getPeak1().getPeakModel().getRetentionTimeAtPeakMaximum() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
			}
		}).create());
		list.add(defaultSortableColumn(Messages.peakResolution, 150, new Function<IPeakResolution, Float>() {

			@Override
			public Float apply(IPeakResolution resolution) {

				return resolution.calculate();
			}
		}).create());
		return list;
	}
}
