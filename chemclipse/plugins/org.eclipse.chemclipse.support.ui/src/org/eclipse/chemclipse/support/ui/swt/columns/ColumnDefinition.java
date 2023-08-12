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
 * Philip Wenig - added cell color support
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.swt.columns;

import java.util.Comparator;
import java.util.function.Function;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.SWT;

public interface ColumnDefinition<DataType, ColumnType> extends Function<DataType, ColumnType> {

	CellLabelProvider getLabelProvider();

	Comparator<ColumnType> getComparator();

	String getTitle();

	int getWidth();

	default EditingSupport getEditingSupport(ColumnViewer columnViewer) {

		return null;
	}

	default int getStyle() {

		return SWT.LEFT;
	}

	default boolean isResizable() {

		return true;
	}

	default int getMinWidth() {

		return getWidth() / 2;
	}
}