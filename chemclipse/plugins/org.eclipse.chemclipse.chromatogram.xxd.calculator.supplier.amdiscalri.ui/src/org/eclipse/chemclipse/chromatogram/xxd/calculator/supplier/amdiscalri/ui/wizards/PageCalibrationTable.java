/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.wizards;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl.RetentionIndexExtractor;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.IRetentionIndexEntry;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.RetentionIndexEntry;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.swt.RetentionIndexTableViewerUI;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.swt.ui.components.chromatogram.SelectedPeakChromatogramUI;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.wizards.AbstractExtendedWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class PageCalibrationTable extends AbstractExtendedWizardPage {

	private static final Logger logger = Logger.getLogger(PageCalibrationTable.class);
	//
	private static final String ACTION_INITIALIZE = "ACTION_INITIALIZE";
	private static final String ACTION_CANCEL = "ACTION_CANCEL";
	private static final String ACTION_DELETE = "ACTION_DELETE";
	private static final String ACTION_ADD = "ACTION_ADD";
	private static final String ACTION_SELECT = "ACTION_SELECT";
	//
	private IRetentionIndexWizardElements wizardElements;
	//
	private Button checkBoxValidateRetentionIndices;
	private SelectedPeakChromatogramUI selectedPeakChromatogramUI;
	//
	private Button buttonCancel;
	private Button buttonDelete;
	private Button buttonAdd;
	//
	private Combo comboReferences;
	private Button buttonAddReference;
	private Text textRetentionTime;
	//
	private RetentionIndexTableViewerUI retentionIndexTableViewerUI;

	public PageCalibrationTable(IRetentionIndexWizardElements wizardElements) {
		//
		super(PageCalibrationTable.class.getName());
		setTitle("Calibration Table");
		setDescription("Please verify the calibration table.");
		this.wizardElements = wizardElements;
	}

	@Override
	public boolean canFinish() {

		if(wizardElements.isRetentionIndexDataValidated()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void setDefaultValues() {

	}

	@Override
	public void setVisible(boolean visible) {

		super.setVisible(visible);
		if(visible) {
			IChromatogramSelectionMSD chromatogramSelectionMSD = wizardElements.getChromatogramSelectionMSD();
			if(chromatogramSelectionMSD != null && chromatogramSelectionMSD.getChromatogramMSD() != null) {
				IChromatogramMSD chromatogramMSD = chromatogramSelectionMSD.getChromatogramMSD();
				selectedPeakChromatogramUI.update(chromatogramSelectionMSD, true);
				RetentionIndexExtractor retentionIndexExtractor = new RetentionIndexExtractor();
				List<IRetentionIndexEntry> extractedRetentionIndexEntries = retentionIndexExtractor.extract(chromatogramMSD);
				wizardElements.setExtractedRetentionIndexEntries(extractedRetentionIndexEntries);
				retentionIndexTableViewerUI.setInput(extractedRetentionIndexEntries);
			}
			validateSelection();
		}
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		//
		createCheckBoxField(composite);
		createChromatogramField(composite);
		createButtonField(composite);
		createAddReferenceField(composite);
		createAddReferenceButton(composite);
		createTableField(composite);
		//
		enableButtonFields(ACTION_INITIALIZE);
		validateSelection();
		setControl(composite);
	}

	private void createCheckBoxField(Composite composite) {

		checkBoxValidateRetentionIndices = new Button(composite, SWT.CHECK);
		checkBoxValidateRetentionIndices.setText("Retention indices are valid.");
		checkBoxValidateRetentionIndices.setSelection(false);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 4;
		checkBoxValidateRetentionIndices.setLayoutData(gridData);
		checkBoxValidateRetentionIndices.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				wizardElements.setRetentionIndexDataIsValidated(checkBoxValidateRetentionIndices.getSelection());
				validateSelection();
			}
		});
	}

	private void createChromatogramField(Composite composite) {

		Composite parent = new Composite(composite, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 4;
		parent.setLayoutData(gridData);
		parent.setLayout(new FillLayout());
		selectedPeakChromatogramUI = new SelectedPeakChromatogramUI(parent, SWT.BORDER);
	}

	private void createButtonField(Composite composite) {

		Label label = new Label(composite, SWT.NONE);
		label.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		label.setLayoutData(gridData);
		/*
		 * Buttons
		 */
		Composite compositeButtons = new Composite(composite, SWT.NONE);
		compositeButtons.setLayout(new GridLayout(3, true));
		GridData gridDataComposite = new GridData();
		gridDataComposite.horizontalAlignment = SWT.RIGHT;
		compositeButtons.setLayoutData(gridDataComposite);
		//
		buttonCancel = new Button(compositeButtons, SWT.PUSH);
		buttonCancel.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CANCEL, IApplicationImage.SIZE_16x16));
		buttonCancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				comboReferences.setText("");
				textRetentionTime.setText("");
				enableButtonFields(ACTION_CANCEL);
			}
		});
		//
		buttonDelete = new Button(compositeButtons, SWT.PUSH);
		buttonDelete.setEnabled(false);
		buttonDelete.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		buttonDelete.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Table table = retentionIndexTableViewerUI.getTable();
				int index = table.getSelectionIndex();
				if(index >= 0) {
					MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING);
					messageBox.setText("Delete reference(s)?");
					messageBox.setMessage("Would you like to delete the reference(s)?");
					if(messageBox.open() == SWT.OK) {
						//
						enableButtonFields(ACTION_DELETE);
						TableItem tableItem = table.getItem(index);
						Object object = tableItem.getData();
						if(object instanceof IRetentionIndexEntry) {
							IRetentionIndexEntry retentionIndexEntry = (IRetentionIndexEntry)object;
							wizardElements.getExtractedRetentionIndexEntries().remove(retentionIndexEntry);
							retentionIndexTableViewerUI.setInput(wizardElements.getExtractedRetentionIndexEntries());
						}
						validateSelection();
					}
				}
			}
		});
		//
		buttonAdd = new Button(compositeButtons, SWT.PUSH);
		buttonAdd.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		buttonAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableButtonFields(ACTION_ADD);
			}
		});
	}

	private void createAddReferenceField(Composite composite) {

		Label labelAlkane = new Label(composite, SWT.NONE);
		labelAlkane.setText("Reference:");
		//
		comboReferences = new Combo(composite, SWT.BORDER);
		comboReferences.setText("");
		comboReferences.setItems(wizardElements.getAvailableStandards());
		comboReferences.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Label labelRetentionTime = new Label(composite, SWT.NONE);
		labelRetentionTime.setText("RT (minutes):");
		//
		textRetentionTime = new Text(composite, SWT.BORDER);
		textRetentionTime.setText("");
		GridData gridDataText = new GridData(GridData.FILL_HORIZONTAL);
		gridDataText.grabExcessHorizontalSpace = true;
		textRetentionTime.setLayoutData(gridDataText);
	}

	private void createAddReferenceButton(Composite composite) {

		buttonAddReference = new Button(composite, SWT.PUSH);
		buttonAddReference.setText("Add reference");
		buttonAddReference.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE_ADD, IApplicationImage.SIZE_16x16));
		GridData gridDataButton = new GridData(GridData.FILL_HORIZONTAL);
		gridDataButton.horizontalSpan = 4;
		buttonAddReference.setLayoutData(gridDataButton);
		buttonAddReference.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					enableButtonFields(ACTION_INITIALIZE);
					//
					String name = comboReferences.getText().trim();
					int retentionTime = (int)(Double.parseDouble(textRetentionTime.getText().trim()) * AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
					float retentionIndex = wizardElements.getRetentionIndex(name);
					//
					comboReferences.setText("");
					textRetentionTime.setText("");
					//
					IRetentionIndexEntry retentionIndexEntry = new RetentionIndexEntry(retentionTime, retentionIndex, name);
					wizardElements.getExtractedRetentionIndexEntries().add(retentionIndexEntry);
					retentionIndexTableViewerUI.setInput(wizardElements.getExtractedRetentionIndexEntries());
					validateSelection();
				} catch(Exception e1) {
					logger.warn(e1);
				}
			}
		});
	}

	private void createTableField(Composite composite) {

		retentionIndexTableViewerUI = new RetentionIndexTableViewerUI(composite, SWT.BORDER | SWT.MULTI);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 4;
		retentionIndexTableViewerUI.getTable().setLayoutData(gridData);
		retentionIndexTableViewerUI.getTable().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableButtonFields(ACTION_SELECT);
				Table table = retentionIndexTableViewerUI.getTable();
				int index = table.getSelectionIndex();
				TableItem tableItem = table.getItem(index);
				Object object = tableItem.getData();
				if(object instanceof IRetentionIndexEntry) {
					IRetentionIndexEntry retentionIndexEntry = (IRetentionIndexEntry)object;
					int retentionTime = retentionIndexEntry.getRetentionTime();
					IChromatogramSelectionMSD chromatogramSelectionMSD = wizardElements.getChromatogramSelectionMSD();
					if(chromatogramSelectionMSD != null && chromatogramSelectionMSD.getChromatogramMSD() != null) {
						IChromatogramMSD chromatogramMSD = chromatogramSelectionMSD.getChromatogramMSD();
						IChromatogramPeakMSD selectedPeak = getSelectedPeak(chromatogramMSD, retentionTime);
						if(selectedPeak != null) {
							chromatogramSelectionMSD.setSelectedPeak(selectedPeak);
							selectedPeakChromatogramUI.update(chromatogramSelectionMSD, true);
						}
					}
				}
			}
		});
	}

	/**
	 * May return null.
	 * 
	 * @param chromatogramMSD
	 * @param retentionTime
	 * @return {@link IChromatogramPeakMSD}
	 */
	private IChromatogramPeakMSD getSelectedPeak(IChromatogramMSD chromatogramMSD, int retentionTime) {

		for(IChromatogramPeakMSD peak : chromatogramMSD.getPeaks()) {
			if(peak.getPeakModel().getRetentionTimeAtPeakMaximum() == retentionTime) {
				return peak;
			}
		}
		return null;
	}

	private void enableButtonFields(String action) {

		enableFields(false);
		switch(action) {
			case ACTION_INITIALIZE:
				buttonAdd.setEnabled(true);
				break;
			case ACTION_CANCEL:
				buttonAdd.setEnabled(true);
				break;
			case ACTION_DELETE:
				buttonAdd.setEnabled(true);
				break;
			case ACTION_ADD:
				buttonCancel.setEnabled(true);
				comboReferences.setEnabled(true);
				textRetentionTime.setEnabled(true);
				buttonAddReference.setEnabled(true);
				break;
			case ACTION_SELECT:
				buttonAdd.setEnabled(true);
				if(retentionIndexTableViewerUI.getTable().getSelectionIndex() >= 0) {
					buttonDelete.setEnabled(true);
				} else {
					buttonDelete.setEnabled(false);
				}
				break;
		}
	}

	private void enableFields(boolean enabled) {

		buttonCancel.setEnabled(enabled);
		buttonDelete.setEnabled(enabled);
		buttonAdd.setEnabled(enabled);
		//
		comboReferences.setEnabled(enabled);
		textRetentionTime.setEnabled(enabled);
		buttonAddReference.setEnabled(enabled);
	}

	private void validateSelection() {

		String message = null;
		if(!checkBoxValidateRetentionIndices.getSelection()) {
			message = "Please verify the data and activate the check box.";
		}
		/*
		 * Updates the status
		 */
		updateStatus(message);
	}
}
