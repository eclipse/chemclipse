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
package org.eclipse.chemclipse.support.ui.swt;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Contains utility methods to construct standard SWT items in a uniform way
 *
 */
public class ControlBuilder {

	public static Composite createContainer(Composite parent) {

		return createContainer(parent, 1);
	}

	public static Composite createContainer(Composite parent, int columns) {

		return createContainer(parent, columns, false);
	}

	/**
	 * Create a "container-composite", that is one with a gridlayout that does not add additional spaces
	 * 
	 * @param parent
	 * @param columns
	 * @param equal
	 * @return
	 */
	public static Composite createContainer(Composite parent, int columns, boolean equal) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(columns, equal);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginTop = 0;
		layout.marginRight = 0;
		composite.setLayout(layout);
		return fill(composite);
	}

	public static <T extends Control> T fill(T control) {

		if(control.getParent().getLayout() instanceof GridLayout) {
			Object layoutData = control.getLayoutData();
			GridData gridData;
			if(layoutData instanceof GridData) {
				gridData = (GridData)layoutData;
			} else {
				gridData = new GridData(SWT.FILL, SWT.CENTER, true, true);
				control.setLayoutData(gridData);
			}
			gridData.grabExcessHorizontalSpace = true;
		}
		return control;
	}

	public static TreeViewer createTreeTable(Composite parent, boolean enableTooltips) {

		TreeViewer treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		treeViewer.setUseHashlookup(true);
		treeViewer.setExpandPreCheckFilters(true);
		treeViewer.getTree().setLinesVisible(true);
		treeViewer.getTree().setHeaderVisible(true);
		if(enableTooltips) {
			ColumnViewerToolTipSupport.enableFor(treeViewer, ToolTip.NO_RECREATE);
		}
		return treeViewer;
	}

	public static TreeViewerColumn createEmptyColumn(TreeViewer treeViewer) {

		return createColumn(treeViewer, "", 1, new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {

			}
		});
	}

	public static TreeViewerColumn createColumn(TreeViewer treeViewer, String text, int width, CellLabelProvider labelProvider) {

		TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.NONE);
		column.getColumn().setText(text);
		column.getColumn().setWidth(width);
		if(labelProvider != null) {
			column.setLabelProvider(labelProvider);
		}
		return column;
	}
}
