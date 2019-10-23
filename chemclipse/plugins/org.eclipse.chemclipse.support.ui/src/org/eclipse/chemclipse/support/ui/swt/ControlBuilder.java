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
import org.eclipse.swt.widgets.Label;

/**
 * Contains utility methods to construct standard SWT items in a uniform way
 *
 */
public class ControlBuilder {

	/**
	 * Create a "container-composite", that is one with a gridlayout that does not add additional spaces and is filled horizontal
	 * 
	 * @return
	 */
	public static Composite createContainer(Composite parent) {

		return createContainer(parent, 1);
	}

	/**
	 * Create a "container-composite", that is one with a gridlayout that does not add additional spaces and is filled horizontal
	 * 
	 * @param parent
	 * @param columns
	 * @return
	 */
	public static Composite createContainer(Composite parent, int columns) {

		return createContainer(parent, columns, false);
	}

	/**
	 * Create a "container-composite", that is one with a gridlayout that does not add additional spaces and is filled horizontal
	 * 
	 * @param parent
	 * @param columns
	 * @param equal
	 * @return
	 */
	public static Composite createContainer(Composite parent, int columns, boolean equal) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setBackgroundMode(SWT.INHERIT_FORCE);
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

	/**
	 * Creates a label suitable for a labelcontainer
	 * 
	 * @param label
	 * @param container
	 * @return
	 */
	public static Label label(String label, Composite container) {

		return label(label, null, container);
	}

	public static Label label(String label, String tooltip, Composite container) {

		Label labelComponent = new Label(container, SWT.NONE);
		labelComponent.setText(label);
		if(tooltip != null) {
			labelComponent.setToolTipText(tooltip);
		}
		return labelComponent;
	}

	/**
	 * Fills the control on the horizontal axis
	 * 
	 * @param control
	 * @return
	 */
	public static <T extends Control> T fill(T control) {

		if(isGridLayouted(control)) {
			GridData data = gridData(control);
			data.horizontalAlignment = SWT.FILL;
			data.grabExcessHorizontalSpace = true;
		}
		return control;
	}

	/**
	 * Maximize the control in the vertical and horizontal axis
	 * 
	 * @param control
	 * @return
	 */
	public static <T extends Control> T maximize(T control) {

		GridData gridData = gridData(control);
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		return control;
	}

	/**
	 * Creates a container, that is meant to hold labels and components in a two columns grid layout, in contrast to {@link #createContainer(Composite)} and its variants, this add spacings around components
	 * 
	 * @param parent
	 * @return
	 */
	public static Composite createLabelContainer(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		composite.setLayout(layout);
		return fill(composite);
	}

	/**
	 * Sets the horizontal span of the given control
	 * 
	 * @param control
	 * @param cols
	 * @return
	 */
	public static <T extends Control> T span(T control, int cols) {

		gridData(control).horizontalSpan = cols;
		return control;
	}

	public static Control createSeperator(Composite parent) {

		Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return separator;
	}

	/**
	 * Checks if a control is inside a grid-layout and thus can have {@link GridData} layout data
	 * 
	 * @param control
	 * @return
	 */
	public static <T extends Control> boolean isGridLayouted(T control) {

		return control.getParent().getLayout() instanceof GridLayout;
	}

	public static GridData gridData(Control control) {

		Object layoutData = control.getLayoutData();
		if(layoutData instanceof GridData) {
			return (GridData)layoutData;
		} else {
			GridData gridData = new GridData();
			if(isGridLayouted(control)) {
				// only set it if the parent is valid....
				control.setLayoutData(gridData);
			}
			// ... but always return it so caller can still set values on it, even though they are ignored
			return gridData;
		}
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
