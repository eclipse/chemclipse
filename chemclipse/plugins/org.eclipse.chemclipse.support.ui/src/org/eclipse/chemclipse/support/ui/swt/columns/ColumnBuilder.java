/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring classifier
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.swt.columns;

import java.util.Comparator;
import java.util.function.Function;

import org.eclipse.jface.viewers.ColumnLabelProvider;

public class ColumnBuilder<DataType, ColumnType> {

	private ColumnLabelProvider labelProvider;
	private String title;
	private int width;
	private Function<DataType, ColumnType> mapper;
	private Comparator<ColumnType> comparator;

	private ColumnBuilder(String title, int width, Function<DataType, ColumnType> mapper) {

		this.title = title;
		this.width = width;
		this.mapper = mapper;
	}

	public ColumnBuilder<DataType, ColumnType> sort(Comparator<ColumnType> comparator) {

		this.comparator = comparator;
		return this;
	}

	public ColumnBuilder<DataType, ColumnType> labelProvider(ColumnLabelProvider labelProvider) {

		this.labelProvider = labelProvider;
		return this;
	}

	public ColumnBuilder<DataType, ColumnType> format(Function<ColumnType, String> formater) {

		return labelProvider(new ColumnLabelProvider() {

			@SuppressWarnings("unchecked")
			@Override
			public String getText(Object element) {

				return formater.apply((ColumnType)element);
			}
		});
	}

	public static <DataType, ColumnType> ColumnBuilder<DataType, ColumnType> column(String title, int width, Function<DataType, ColumnType> mapper) {

		return new ColumnBuilder<DataType, ColumnType>(title, width, mapper);
	}

	public static <DataType, ColumnType extends Comparable<ColumnType>> ColumnBuilder<DataType, ColumnType> defaultSortableColumn(String title, int width, Function<DataType, ColumnType> mapper) {

		return new ColumnBuilder<DataType, ColumnType>(title, width, mapper).sort(new Comparator<ColumnType>() {

			@Override
			public int compare(ColumnType o1, ColumnType o2) {

				return o1.compareTo(o2);
			}
		});
	}

	public ColumnDefinition<DataType, ColumnType> create() {

		return new SimpleColumnDefinition<DataType, ColumnType>(title, width, labelProvider, comparator, mapper);
	}
}