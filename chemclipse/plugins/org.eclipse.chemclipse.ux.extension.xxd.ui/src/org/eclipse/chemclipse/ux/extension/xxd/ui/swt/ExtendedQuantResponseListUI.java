/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import org.eclipse.chemclipse.model.quantitation.IConcentrationResponseEntries;
import org.eclipse.chemclipse.model.quantitation.IConcentrationResponseEntry;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.dialogs.ConcentrationResponseEntryEdit;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.dialogs.ConcentrationResponseEntryEditDialog;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePagePeaksAxes;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ExtendedQuantResponseListUI extends Composite {

	private static final String DESCRIPTION = "Quantitation Response";
	//
	@SuppressWarnings("rawtypes")
	private IQuantitationCompound quantitationCompound;
	private QuantResponseListUI quantResponseListUI;

	public ExtendedQuantResponseListUI(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	@SuppressWarnings("rawtypes")
	public void update(IQuantitationCompound quantitationCompound) {

		this.quantitationCompound = quantitationCompound;
		setQuantitationCompound();
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(composite);
		createTable(composite);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		createAddButton(composite);
		createEditButton(composite);
		createSettingsButton(composite);
	}

	private void createAddButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Add a new response entry.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(quantitationCompound != null) {
					String concentrationUnit = quantitationCompound.getConcentrationUnit();
					ConcentrationResponseEntryEdit concentrationResponseEntryEdit = new ConcentrationResponseEntryEdit(concentrationUnit);
					ConcentrationResponseEntryEditDialog dialog = new ConcentrationResponseEntryEditDialog(e.display.getActiveShell(), concentrationResponseEntryEdit, "Create a new concentration response entry.");
					if(dialog.open() == IDialogConstants.OK_ID) {
						IConcentrationResponseEntry concentrationResponseEntry = concentrationResponseEntryEdit.getConcentrationResponseEntry();
						if(concentrationResponseEntry != null) {
							quantitationCompound.getConcentrationResponseEntries().add(concentrationResponseEntry);
							setQuantitationCompound();
						}
					}
				} else {
					MessageDialog.openError(e.display.getActiveShell(), DESCRIPTION, "Please select a quantitation compound.");
				}
			}
		});
	}

	private void createEditButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Edit a response entry.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(quantitationCompound != null) {
					IConcentrationResponseEntry concentrationResponseEntryOld = getSelectedConcentrationResponseEntry();
					if(concentrationResponseEntryOld != null) {
						String concentrationUnit = quantitationCompound.getConcentrationUnit();
						ConcentrationResponseEntryEdit concentrationResponseEntryEdit = new ConcentrationResponseEntryEdit(concentrationUnit);
						concentrationResponseEntryEdit.setConcentrationResponseEntryMSD(concentrationResponseEntryOld);
						ConcentrationResponseEntryEditDialog dialog = new ConcentrationResponseEntryEditDialog(e.display.getActiveShell(), concentrationResponseEntryEdit, "Edit the concentration response entry.");
						if(dialog.open() == IDialogConstants.OK_ID) {
							/*
							 * Save the edited response entry.
							 */
							IConcentrationResponseEntry concentrationResponseEntryNew = concentrationResponseEntryEdit.getConcentrationResponseEntry();
							if(concentrationResponseEntryOld != null) {
								IConcentrationResponseEntries concentrationResponseEntriesMSD = quantitationCompound.getConcentrationResponseEntries();
								concentrationResponseEntriesMSD.remove(concentrationResponseEntryOld);
								concentrationResponseEntriesMSD.add(concentrationResponseEntryNew);
								quantitationCompound.updateConcentrationResponseEntries(concentrationResponseEntriesMSD);
								setQuantitationCompound();
							}
						}
					} else {
						MessageDialog.openError(e.display.getActiveShell(), DESCRIPTION, "Please select a concentration response entry.");
					}
				} else {
					MessageDialog.openError(e.display.getActiveShell(), DESCRIPTION, "Please select a quantitation compound previously.");
				}
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", new PreferencePagePeaksAxes()));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(e.display.getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(e.display.getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private void createTable(Composite parent) {

		quantResponseListUI = new QuantResponseListUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		quantResponseListUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void applySettings() {

		setQuantitationCompound();
	}

	private void setQuantitationCompound() {

		if(quantitationCompound != null) {
			quantResponseListUI.setInput(quantitationCompound.getConcentrationResponseEntries());
		} else {
			quantResponseListUI.clear();
		}
	}

	private IConcentrationResponseEntry getSelectedConcentrationResponseEntry() {

		IConcentrationResponseEntry quantitationSignal = null;
		Object element = quantResponseListUI.getStructuredSelection().getFirstElement();
		if(element instanceof IConcentrationResponseEntry) {
			quantitationSignal = (IConcentrationResponseEntry)element;
		}
		return quantitationSignal;
	}
}
