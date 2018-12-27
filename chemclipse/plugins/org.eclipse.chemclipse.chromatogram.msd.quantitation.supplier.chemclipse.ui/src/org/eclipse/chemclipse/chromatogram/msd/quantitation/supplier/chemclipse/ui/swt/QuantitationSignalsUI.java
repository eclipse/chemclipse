/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabase;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.controller.QuantitationSignalEntryEdit;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.provider.QuantitationSignalsContentProvider;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.provider.QuantitationSignalsLabelProvider;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.provider.QuantitationSignalsTableComparator;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.runnables.dialogs.QuantitationSignalEntryEditDialog;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignal;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignals;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationCompoundMSD;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class QuantitationSignalsUI extends AbstractTableViewerUI implements IQuantitationCompoundUpdater {

	private static final String MESSAGE_BOX_TEXT = "Quantitation Signal";
	private IQuantitationCompoundMSD quantitationCompoundDocument;

	public QuantitationSignalsUI(Composite parent, int style) {
		parent.setLayout(new FillLayout());
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		//
		addList(composite);
		addButtons(composite);
	}

	@Override
	public void update(IQuantitationCompoundMSD quantitationCompoundDocument, IQuantDatabase database) {

		this.quantitationCompoundDocument = quantitationCompoundDocument;
		//
		if(quantitationCompoundDocument == null) {
			getTableViewer().setInput(null);
		} else {
			setTableViewerInput();
		}
	}

	private void setTableViewerInput() {

		getTableViewer().setInput(quantitationCompoundDocument);
	}

	private void addList(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridDataTable = new GridData(GridData.FILL_BOTH);
		composite.setLayout(new GridLayout(1, true));
		composite.setLayoutData(gridDataTable);
		//
		String[] titles = {"Ion", "Relative Response", "Uncertainty", "Use"};
		int bounds[] = {100, 100, 100, 100};
		IStructuredContentProvider contentProvider = new QuantitationSignalsContentProvider();
		ILabelProvider labelProvider = new QuantitationSignalsLabelProvider();
		QuantitationSignalsTableComparator viewerTableComparator = new QuantitationSignalsTableComparator();
		//
		createTableViewer(composite, gridDataTable, contentProvider, labelProvider, viewerTableComparator, titles, bounds);
		setTableViewerInput();
	}

	private void addButtons(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		GridData gridDataButtons = new GridData(GridData.FILL_VERTICAL);
		gridDataButtons.verticalAlignment = SWT.TOP;
		composite.setLayoutData(gridDataButtons);
		//
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		addButtonAdd(composite, gridData);
		addButtonEdit(composite, gridData);
		addButtonRemove(composite, gridData);
		addButtonRemoveAll(composite, gridData);
	}

	private void addButtonAdd(Composite parent, GridData gridData) {

		Button button = new Button(parent, SWT.PUSH);
		button.setLayoutData(gridData);
		button.setText("Add");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * The selected compound must be not null.
				 */
				if(quantitationCompoundDocument != null) {
					/*
					 * Try to add a new response entry.
					 */
					Shell shell = Display.getCurrent().getActiveShell();
					QuantitationSignalEntryEdit quantitationSignalEntryEdit = new QuantitationSignalEntryEdit();
					QuantitationSignalEntryEditDialog dialog = new QuantitationSignalEntryEditDialog(shell, quantitationSignalEntryEdit, "Create a new quantitation signal.");
					if(dialog.open() == IDialogConstants.OK_ID) {
						/*
						 * Save the response entry.
						 */
						IQuantitationSignal quantitationSignalMSD = quantitationSignalEntryEdit.getQuantitationSignalMSD();
						if(quantitationSignalMSD != null) {
							quantitationCompoundDocument.getQuantitationSignalsMSD().add(quantitationSignalMSD);
							setTableViewerInput();
						}
					}
				} else {
					showMessage(MESSAGE_BOX_TEXT, "Please select a quantitation compound previously.");
				}
			}
		});
	}

	private void addButtonEdit(Composite parent, GridData gridData) {

		Button button = new Button(parent, SWT.PUSH);
		button.setLayoutData(gridData);
		button.setText("Edit");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * The selected compound must be not null.
				 */
				if(quantitationCompoundDocument != null) {
					/*
					 * Try to edit a new response entry.
					 */
					IQuantitationSignal quantitationSignalOld = getSelectedQuantitationSignalMSD();
					if(quantitationSignalOld != null) {
						/*
						 * Edit
						 */
						Shell shell = Display.getCurrent().getActiveShell();
						QuantitationSignalEntryEdit quantitationSignalEntryEdit = new QuantitationSignalEntryEdit();
						quantitationSignalEntryEdit.setQuantitationSignalMSD(quantitationSignalOld);
						QuantitationSignalEntryEditDialog dialog = new QuantitationSignalEntryEditDialog(shell, quantitationSignalEntryEdit, "Edit the quantitation signal.");
						if(dialog.open() == IDialogConstants.OK_ID) {
							/*
							 * Save the edited response entry.
							 */
							IQuantitationSignal quantitationSignalNew = quantitationSignalEntryEdit.getQuantitationSignalMSD();
							if(quantitationSignalOld != null) {
								IQuantitationSignals quantitationSignalsMSD = quantitationCompoundDocument.getQuantitationSignalsMSD();
								quantitationSignalsMSD.remove(quantitationSignalOld);
								quantitationSignalsMSD.add(quantitationSignalNew);
								quantitationCompoundDocument.updateQuantitationSignalsMSD(quantitationSignalsMSD);
								setTableViewerInput();
							}
						}
					} else {
						showMessage(MESSAGE_BOX_TEXT, "Please select a quantitation signal.");
					}
				} else {
					showMessage(MESSAGE_BOX_TEXT, "Please select a quantitation compound previously.");
				}
			}
		});
	}

	private void addButtonRemove(Composite parent, GridData gridData) {

		Button button = new Button(parent, SWT.PUSH);
		button.setLayoutData(gridData);
		button.setText("Delete");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				List<IQuantitationSignal> quantitationSignalsMSD = getSelectedQuantitationSignalsMSD();
				if(quantitationSignalsMSD.size() > 0) {
					/*
					 * Remove the selected entries
					 */
					int decision = showQuestion(MESSAGE_BOX_TEXT, "Would you like to delete the selected quantitation signals?");
					if(decision == SWT.YES) {
						quantitationCompoundDocument.getQuantitationSignalsMSD().removeAll(quantitationSignalsMSD);
						setTableViewerInput();
					}
				} else {
					showMessage(MESSAGE_BOX_TEXT, "Please select quantitation signals to delete.");
				}
			}
		});
	}

	private void addButtonRemoveAll(Composite parent, GridData gridData) {

		Button button = new Button(parent, SWT.PUSH);
		button.setLayoutData(gridData);
		button.setText("Delete All");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * Remove all
				 */
				int decision = showQuestion(MESSAGE_BOX_TEXT, "Would you like to delete all response entry?");
				if(decision == SWT.YES) {
					quantitationCompoundDocument.getQuantitationSignalsMSD().clear();
					setTableViewerInput();
				}
			}
		});
	}

	/**
	 * Returns the selected item or null if none is available.
	 * 
	 * @return
	 */
	private IQuantitationSignal getSelectedQuantitationSignalMSD() {

		IQuantitationSignal quantitationSignalMSD = null;
		Object element = getSelectedTableItem();
		if(element instanceof IQuantitationSignal) {
			quantitationSignalMSD = (IQuantitationSignal)element;
		}
		return quantitationSignalMSD;
	}

	/**
	 * Returns the selected items or an empty list none are available.
	 * 
	 * @return
	 */
	private List<IQuantitationSignal> getSelectedQuantitationSignalsMSD() {

		List<IQuantitationSignal> quantitationSignalsMSD = new ArrayList<IQuantitationSignal>();
		List<Object> elements = getSelectedTableItems();
		for(Object element : elements) {
			if(element instanceof IQuantitationSignal) {
				IQuantitationSignal quantitationSignalMSD = (IQuantitationSignal)element;
				quantitationSignalsMSD.add(quantitationSignalMSD);
			}
		}
		return quantitationSignalsMSD;
	}
}
