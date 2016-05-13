/*******************************************************************************
 * Copyright (c) 2016 Lablicate UG (haftungsbeschränkt).
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.List;

import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.OverlaySettingsCheckBoxEditingSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.OverlaySettingsLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.OverlaySettingsTableSorter;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.Composite;

public class OverlaySettingsTableViewer extends EnhancedTableViewer {

	private static final String OVERLAY = "Overlay";
	private String[] TITLES = {"Chromatogram", OVERLAY};
	private int[] BOUNDS = {250, 50};

	public OverlaySettingsTableViewer(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	private void createColumns() {

		/*
		 * Set the provider.
		 */
		setColumns(TITLES, BOUNDS);
		setLabelProvider(new OverlaySettingsLabelProvider());
		setContentProvider(new ListContentProvider());
		setSorter(new OverlaySettingsTableSorter());
		setEditingSupport();
	}

	private void setEditingSupport() {

		TableViewer tableViewer = getTableViewer();
		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		for(int i = 0; i < tableViewerColumns.size(); i++) {
			TableViewerColumn tableViewerColumn = tableViewerColumns.get(i);
			String label = tableViewerColumn.getColumn().getText();
			if(label.equals(OVERLAY)) {
				tableViewerColumn.setEditingSupport(new OverlaySettingsCheckBoxEditingSupport(tableViewer));
			}
		}
	}
}