/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.swt.columns;

import java.util.Comparator;
import java.util.function.Function;

import org.eclipse.jface.viewers.ColumnLabelProvider;

public class SimpleColumnDefinition<DataType, ColumnType> implements ColumnDefinition<DataType, ColumnType> {

	private String title;
	private int width;
	private ColumnLabelProvider labelProvider;
	private Comparator<ColumnType> comparator;
	private Function<DataType, ColumnType> mapper;

	public SimpleColumnDefinition(String title, int width, ColumnLabelProvider labelProvider, Comparator<ColumnType> comparator, Function<DataType, ColumnType> mapper) {
		this.title = title;
		this.width = width;
		this.labelProvider = labelProvider;
		this.comparator = comparator;
		this.mapper = mapper;
	}

	@Override
	public ColumnType apply(DataType t) {

		return mapper.apply(t);
	}

	@Override
	public ColumnLabelProvider getLabelProvider() {

		return labelProvider;
	}

	@Override
	public Comparator<ColumnType> getComparator() {

		return comparator;
	}

	@Override
	public String getTitle() {

		return title;
	}

	@Override
	public int getWidth() {

		return width;
	}
}
