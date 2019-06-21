/*******************************************************************************
 * Copyright (c) 2017, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - extend API
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.swt;

import java.util.List;

import org.eclipse.chemclipse.support.ui.swt.columns.ColumnDefinition;
import org.eclipse.chemclipse.support.ui.swt.columns.ColumnDefinitionProvider;
import org.eclipse.jface.viewers.TableViewerColumn;

public interface IExtendedTableViewer {

	ITableSettings getTableSettings();

	void applySettings(ITableSettings chartSettings);

	/**
	 * @deprecated use {@link #addColumns(ColumnDefinitionProvider)} or {@link #addColumn(ColumnDefinition)} instead
	 * @param titles
	 * @param bounds
	 */
	@Deprecated
	void createColumns(String[] titles, int[] bounds);

	List<TableViewerColumn> getTableViewerColumns();

	boolean isEditEnabled();

	void setEditEnabled(boolean editEnabled);

	void clearColumns();

	<D, C> TableViewerColumn addColumn(ColumnDefinition<D, C> definition);

	default void addColumns(ColumnDefinitionProvider provider) {

		for(ColumnDefinition<?, ?> definition : provider.getColumnDefinitions()) {
			addColumn(definition);
		}
	}
}
