/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.swt;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.IBatchProcessJob;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.converter.model.ChromatogramInputEntry;
import org.eclipse.chemclipse.converter.model.IChromatogramInputEntry;
import org.eclipse.chemclipse.model.handler.IModificationHandler;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.wizards.IChromatogramWizardElements;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputEntriesWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputWizardSettings;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

public class ExtendedChromatogramUI extends Composite {

	private ChromatogramListUI chromatogramListUI;
	//
	private IModificationHandler modificationHandler = null;
	private IBatchProcessJob batchProcessJob;

	public ExtendedChromatogramUI(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	public void update(IBatchProcessJob batchProcessJob) {

		this.batchProcessJob = batchProcessJob;
		setProcessFiles();
	}

	public void setModificationHandler(IModificationHandler modificationHandler) {

		this.modificationHandler = modificationHandler;
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		//
		createChromatogramList(composite);
		createButtons(composite);
	}

	private void createChromatogramList(Composite parent) {

		chromatogramListUI = new ChromatogramListUI(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		chromatogramListUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void createButtons(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(4, false));
		//
		createMoveUpButton(composite);
		createMoveDownButton(composite);
		createRemoveButton(composite);
		createAddButton(composite);
	}

	private void createMoveUpButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Move import record(s) up");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_UP_2, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = chromatogramListUI.getTable().getSelectionIndex();
				if(index != -1) {
					//
					setProcessFiles();
					setEditorDirty(true);
				}
			}
		});
	}

	private void createMoveDownButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Move import record(s) down");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_DOWN_2, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = chromatogramListUI.getTable().getSelectionIndex();
				if(index != -1) {
					//
					setProcessFiles();
					setEditorDirty(true);
				}
			}
		});
	}

	private void createRemoveButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Remove the import record(s)");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_REMOVE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(batchProcessJob != null) {
					TableItem[] tableItems = chromatogramListUI.getTable().getSelection();
					for(TableItem tableItem : tableItems) {
						Object object = tableItem.getData();
						if(object instanceof IChromatogramInputEntry) {
							IChromatogramInputEntry entry = (IChromatogramInputEntry)object;
							batchProcessJob.getChromatogramInputEntries().remove(entry);
						}
					}
					setEditorDirty(true);
				}
				setProcessFiles();
			}
		});
	}

	private void createAddButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Add new chromatogram(s)");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(batchProcessJob != null) {
					InputWizardSettings inputWizardSettings = new InputWizardSettings(new InputWizardSettings.DataType[]{InputWizardSettings.DataType.MSD_CHROMATOGRAM, InputWizardSettings.DataType.CSD_CHROMATOGRAM, InputWizardSettings.DataType.WSD_CHROMATOGRAM});
					inputWizardSettings.setTitle("Chromatogram");
					inputWizardSettings.setDescription("Select chromatogram(s) to analyze.");
					inputWizardSettings.setPathPreferences(PreferenceSupplier.INSTANCE().getPreferences(), PreferenceSupplier.P_FILTER_PATH_IMPORT_RECORDS);
					//
					InputEntriesWizard inputWizard = new InputEntriesWizard(inputWizardSettings);
					WizardDialog wizardDialog = new WizardDialog(e.widget.getDisplay().getActiveShell(), inputWizard);
					wizardDialog.setMinimumPageSize(InputWizardSettings.DEFAULT_WIDTH, InputWizardSettings.DEFAULT_HEIGHT);
					wizardDialog.create();
					//
					if(wizardDialog.open() == WizardDialog.OK) {
						IChromatogramWizardElements chromatogramWizardElements = inputWizard.getChromatogramWizardElements();
						for(String selectedChromatogram : chromatogramWizardElements.getSelectedChromatograms()) {
							File file = new File(selectedChromatogram);
							if(file.exists()) {
								if(batchProcessJob != null) {
									batchProcessJob.getChromatogramInputEntries().add(new ChromatogramInputEntry(file.getAbsolutePath()));
								}
							}
						}
						//
						setEditorDirty(true);
						setProcessFiles();
					}
				}
			}
		});
	}

	private void setProcessFiles() {

		if(batchProcessJob != null) {
			chromatogramListUI.setInput(batchProcessJob.getChromatogramInputEntries());
		} else {
			chromatogramListUI.setInput(null);
		}
	}

	private void setEditorDirty(boolean dirty) {

		if(modificationHandler != null) {
			modificationHandler.setDirty(dirty);
		}
	}
}
