/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
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

	public static final String OVERLAY_SELECTED = "Overlay";
	public static final String LOCK_OFFSET = "Lock Offset";
	//
	private String[] TITLES = {"Chromatogram", OVERLAY_SELECTED, LOCK_OFFSET};
	private int[] BOUNDS = {250, 100, 100};

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
			if(label.equals(OVERLAY_SELECTED)) {
				tableViewerColumn.setEditingSupport(new OverlaySettingsCheckBoxEditingSupport(tableViewer, OVERLAY_SELECTED));
			} else if(label.equals(LOCK_OFFSET)) {
				tableViewerColumn.setEditingSupport(new OverlaySettingsCheckBoxEditingSupport(tableViewer, LOCK_OFFSET));
			}
		}
	}
}