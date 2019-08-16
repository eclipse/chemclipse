/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.swt;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class ExtensionsTileSelectionDialog extends Dialog {

	private IConfigurationElement[] elements;
	private CellLabelProvider labelProvider;
	private IConfigurationElement selectedElement;

	public ExtensionsTileSelectionDialog(Shell parentShell, IConfigurationElement[] elements, CellLabelProvider labelProvider) {
		super(parentShell);
		this.elements = elements;
		this.labelProvider = labelProvider;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		TableViewer tableViewer = new TableViewer(composite);
		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.setLabelProvider(labelProvider);
		column.getColumn().setWidth(300);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setInput(elements);
		Control control = tableViewer.getControl();
		control.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				ISelection selection = event.getSelection();
				if(selection instanceof IStructuredSelection) {
					Object selected = ((IStructuredSelection)selection).getFirstElement();
					if(selected instanceof IConfigurationElement) {
						selectedElement = (IConfigurationElement)selected;
					} else {
						selectedElement = null;
					}
				}
			}
		});
		return composite;
	}

	public IConfigurationElement getSelectedElement() {

		return selectedElement;
	}
}
