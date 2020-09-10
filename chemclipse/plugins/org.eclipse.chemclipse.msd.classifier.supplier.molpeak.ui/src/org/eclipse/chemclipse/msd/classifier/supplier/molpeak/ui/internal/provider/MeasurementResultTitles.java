/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
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
package org.eclipse.chemclipse.msd.classifier.supplier.molpeak.ui.internal.provider;

import static org.eclipse.chemclipse.support.ui.swt.columns.ColumnBuilder.defaultSortableColumn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.chemclipse.support.ui.swt.columns.ColumnDefinition;
import org.eclipse.chemclipse.support.ui.swt.columns.ColumnDefinitionProvider;

public class MeasurementResultTitles implements ColumnDefinitionProvider {

	@Override
	public Collection<? extends ColumnDefinition<?, ?>> getColumnDefinitions() {

		List<ColumnDefinition<?, ?>> list = new ArrayList<>();
		list.add(defaultSortableColumn("Type", 250, new Function<Map.Entry<String, Double>, String>() {

			@Override
			public String apply(Map.Entry<String, Double> entry) {

				return entry.getKey();
			}
		}).create());
		//
		list.add(defaultSortableColumn("Precentage [%]", 150, new Function<Map.Entry<String, Double>, Double>() {

			@Override
			public Double apply(Map.Entry<String, Double> entry) {

				return entry.getValue();
			}
		}).create());
		//
		list.add(defaultSortableColumn("Note", 100, new Function<Map.Entry<String, Double>, String>() {

			@Override
			public String apply(Map.Entry<String, Double> entry) {

				return "";
			}
		}).create());
		//
		return list;
	}
}
