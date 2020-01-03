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

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.SWT;

public class SimpleColumnDefinition<DataType, ColumnType> implements ColumnDefinition<DataType, ColumnType> {

	private String title;
	private int width;
	private CellLabelProvider labelProvider;
	private Comparator<ColumnType> comparator;
	private Function<DataType, ColumnType> mapper;
	private Function<ColumnViewer, EditingSupport> editingSupportSupplier;
	private int style = SWT.LEFT;
	private boolean resizable = true;
	private int minWidth = -1;

	public SimpleColumnDefinition(String title, int width, Function<DataType, String> extractor) {
		this(title, width, new ColumnLabelProvider() {

			@SuppressWarnings("unchecked")
			@Override
			public String getText(Object element) {

				String string = extractor.apply((DataType)element);
				return string == null ? "" : string;
			}
		}, null, null);
	}

	public SimpleColumnDefinition(String title, int width, CellLabelProvider labelProvider) {
		this(title, width, labelProvider, null, null);
	}

	public SimpleColumnDefinition(String title, int width, CellLabelProvider labelProvider, Comparator<ColumnType> comparator, Function<DataType, ColumnType> mapper) {
		this.title = title;
		this.width = width;
		this.labelProvider = labelProvider;
		this.comparator = comparator;
		this.mapper = mapper;
	}

	@Override
	public ColumnType apply(DataType t) {

		if(mapper != null) {
			return mapper.apply(t);
		} else {
			return null;
		}
	}

	@Override
	public CellLabelProvider getLabelProvider() {

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

	@Override
	public EditingSupport getEditingSupport(ColumnViewer columnViewer) {

		if(editingSupportSupplier != null) {
			return editingSupportSupplier.apply(columnViewer);
		}
		return ColumnDefinition.super.getEditingSupport(columnViewer);
	}

	public SimpleColumnDefinition<DataType, ColumnType> withEditingSupport(Function<ColumnViewer, EditingSupport> editingSupportSupplier) {

		this.editingSupportSupplier = editingSupportSupplier;
		return this;
	}

	@Override
	public int getStyle() {

		return style;
	}

	public SimpleColumnDefinition<DataType, ColumnType> withStyle(int style) {

		this.style = style;
		return this;
	}

	@Override
	public boolean isResizable() {

		return resizable;
	}

	public SimpleColumnDefinition<DataType, ColumnType> resizable(boolean resizable) {

		this.resizable = resizable;
		return this;
	}

	@Override
	public int getMinWidth() {

		if(minWidth >= 0) {
			return minWidth;
		}
		return ColumnDefinition.super.getMinWidth();
	}

	public SimpleColumnDefinition<DataType, ColumnType> minWidth(int minWidth) {

		this.minWidth = minWidth;
		return this;
	}
}
