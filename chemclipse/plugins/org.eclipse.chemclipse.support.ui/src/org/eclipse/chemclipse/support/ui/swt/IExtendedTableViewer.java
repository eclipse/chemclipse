/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.swt;

import java.util.List;

import org.eclipse.jface.viewers.TableViewerColumn;

public interface IExtendedTableViewer {

	ITableSettings getTableSettings();

	void applySettings(ITableSettings chartSettings);

	void createColumns(String[] titles, int[] bounds);

	List<TableViewerColumn> getTableViewerColumns();

	boolean isEditEnabled();

	void setEditEnabled(boolean editEnabled);
}
