/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - adjust to new API
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.ui.internal.provider;

import static org.eclipse.chemclipse.support.ui.swt.columns.ColumnBuilder.defaultSortableColumn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.IWncIon;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.ui.l10n.Messages;
import org.eclipse.chemclipse.support.ui.swt.columns.ColumnDefinition;
import org.eclipse.chemclipse.support.ui.swt.columns.ColumnDefinitionProvider;

public class MeasurementResultTitles implements ColumnDefinitionProvider {

	@Override
	public Collection<? extends ColumnDefinition<?, ?>> getColumnDefinitions() {

		List<ColumnDefinition<?, ?>> list = new ArrayList<>();
		list.add(defaultSortableColumn(Messages.name, 250, new Function<IWncIon, String>() {

			@Override
			public String apply(IWncIon ion) {

				return ion.getName();
			}
		}).create());
		list.add(defaultSortableColumn(Messages.ion, 100, new Function<IWncIon, Integer>() {

			@Override
			public Integer apply(IWncIon ion) {

				return ion.getIon();
			}
		}).create());
		list.add(defaultSortableColumn(Messages.percentageSumIntensity, 100, new Function<IWncIon, Double>() {

			@Override
			public Double apply(IWncIon ion) {

				return ion.getPercentageSumIntensity();
			}
		}).create());
		list.add(defaultSortableColumn(Messages.percentageMaxIntensity, 100, new Function<IWncIon, Double>() {

			@Override
			public Double apply(IWncIon ion) {

				return ion.getPercentageMaxIntensity();
			}
		}).create());
		return list;
	}
}
