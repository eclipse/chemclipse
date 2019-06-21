/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - adjust to new API
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.ui.internal.provider;

import static org.eclipse.chemclipse.support.ui.swt.columns.ColumnBuilder.defaultSortableColumn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;
import org.eclipse.chemclipse.support.ui.swt.columns.ColumnDefinition;
import org.eclipse.chemclipse.support.ui.swt.columns.ColumnDefinitionProvider;

public class ResultTitles implements ColumnDefinitionProvider {

	@Override
	public Collection<? extends ColumnDefinition<?, ?>> getColumnDefinitions() {

		List<ColumnDefinition<?, ?>> list = new ArrayList<>();
		list.add(defaultSortableColumn("Start Scan", 150, new Function<ICombinedMassSpectrum, Integer>() {

			@Override
			public Integer apply(ICombinedMassSpectrum spectrum) {

				return spectrum.getStartScan();
			}
		}).create());
		list.add(defaultSortableColumn("Stop Scan", 150, new Function<ICombinedMassSpectrum, Integer>() {

			@Override
			public Integer apply(ICombinedMassSpectrum spectrum) {

				return spectrum.getStopScan();
			}
		}).create());
		list.add(defaultSortableColumn("Start RT", 150, new Function<ICombinedMassSpectrum, Integer>() {

			@Override
			public Integer apply(ICombinedMassSpectrum spectrum) {

				return spectrum.getStartRetentionTime();
			}
		}).create());
		list.add(defaultSortableColumn("Stop RT", 150, new Function<ICombinedMassSpectrum, Integer>() {

			@Override
			public Integer apply(ICombinedMassSpectrum spectrum) {

				return spectrum.getStopRetentionTime();
			}
		}).create());
		list.add(defaultSortableColumn("Start RI", 150, new Function<ICombinedMassSpectrum, Float>() {

			@Override
			public Float apply(ICombinedMassSpectrum spectrum) {

				return spectrum.getStartRetentionIndex();
			}
		}).create());
		list.add(defaultSortableColumn("Stop RI", 150, new Function<ICombinedMassSpectrum, Float>() {

			@Override
			public Float apply(ICombinedMassSpectrum spectrum) {

				return spectrum.getStopRetentionIndex();
			}
		}).create());
		return list;
	}
}
