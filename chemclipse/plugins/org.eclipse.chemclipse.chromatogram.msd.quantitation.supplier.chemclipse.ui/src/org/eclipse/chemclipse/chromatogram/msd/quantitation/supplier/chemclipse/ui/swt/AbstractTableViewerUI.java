/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.swt;

import java.util.List;

import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractTableViewerUI {

	private ExtendedTableViewer tableViewer;

	public void setFocus() {

		tableViewer.getControl().setFocus();
	}

	public void createTableViewer(Composite parent, GridData gridData, IStructuredContentProvider contentProvider, ILabelProvider labelProvider, AbstractRecordTableComparator viewerTableComparator, final String[] titles, int[] bounds) {

		tableViewer = new ExtendedTableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.getControl().setLayoutData(gridData);
		tableViewer.createColumns(titles, bounds);
		tableViewer.setContentProvider(contentProvider);
		tableViewer.setLabelProvider(labelProvider);
		tableViewer.setComparator(viewerTableComparator);
		/*
		 * Copy the table content.
		 */
		tableViewer.getTable().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == 99 && e.stateMask == 262144) {
					/*
					 * The selected content will be placed to the clipboard if
					 * the user is using "Function + c". "Function-Key" 262144
					 * (stateMask) + "c" 99 (keyCode)
					 */
					tableViewer.copyToClipboard(titles);
				}
			}
		});
	}

	public TableViewer getTableViewer() {

		return tableViewer;
	}

	protected void showMessage(String text, String message) {

		Shell shell = Display.getCurrent().getActiveShell();
		MessageBox messageBox = new MessageBox(shell, SWT.OK);
		messageBox.setText(text);
		messageBox.setMessage(message);
		messageBox.open();
	}

	protected int showQuestion(String text, String message) {

		Shell shell = Display.getCurrent().getActiveShell();
		MessageBox messageBox = new MessageBox(shell, SWT.YES | SWT.NO | SWT.CANCEL);
		messageBox.setText(text);
		messageBox.setMessage(message);
		return messageBox.open();
	}

	/**
	 * Returns the selected item of the table viewer.
	 * 
	 * @return Object
	 */
	protected Object getSelectedTableItem() {

		IStructuredSelection structuredSelection = (IStructuredSelection)getTableViewer().getSelection();
		Object element = structuredSelection.getFirstElement();
		return element;
	}

	/**
	 * Returns the selected items of the table viewer.
	 * 
	 * @return Object
	 */
	@SuppressWarnings("unchecked")
	protected List<Object> getSelectedTableItems() {

		IStructuredSelection structuredSelection = (IStructuredSelection)getTableViewer().getSelection();
		return structuredSelection.toList();
	}
}
