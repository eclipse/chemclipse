/*******************************************************************************
 * Copyright (c) 2016, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.wizards;

import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.autoComplete;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl.AlkaneIdentifier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.IPeakComparisonResult;
import org.eclipse.chemclipse.model.identifier.IPeakLibraryInformation;
import org.eclipse.chemclipse.model.identifier.PeakComparisonResult;
import org.eclipse.chemclipse.model.identifier.PeakLibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedCombo;
import org.eclipse.chemclipse.support.ui.wizards.AbstractExtendedWizardPage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.PeakTableRetentionIndexViewerUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.PeakTargetsViewerUI;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

public class PagePeakAssignment extends AbstractExtendedWizardPage {

	private static final Logger logger = Logger.getLogger(PagePeakAssignment.class);
	//
	private RetentionIndexWizardElements wizardElements;
	private PeakTableRetentionIndexViewerUI peakTableViewerUI;
	private PeakTargetsViewerUI targetsViewerUI;
	private Label labelIndexRange;
	private Button buttonPrevious;
	private Text textCurrentIndexName;
	private Button buttonNext;
	private String[] availableStandards;
	private int indexSelectedStandard;
	//
	private String databaseName;
	//
	private static final int ACTION_INCREASE_INDEX = 1;
	private static final int ACTION_DECREASE_INDEX = 2;

	public PagePeakAssignment(RetentionIndexWizardElements wizardElements) {

		super(PagePeakAssignment.class.getName());
		setTitle("Peak Assigment");
		setDescription("Please assign the alkanes.");
		this.wizardElements = wizardElements;
		availableStandards = wizardElements.getAvailableStandards();
		//
		File file = new File(AlkaneIdentifier.getDatabase());
		databaseName = file.getName();
	}

	@Override
	public boolean canFinish() {

		if(getMessage() == null) {
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
			IChromatogramSelection<?, ?> chromatogramSelection = wizardElements.getChromatogramSelection();
			if(chromatogramSelection != null && chromatogramSelection.getChromatogram() != null) {
				/*
				 * Set the start index.
				 */
				indexSelectedStandard = getStartIndex(availableStandards, wizardElements.getStartIndexName());
				if(indexSelectedStandard > -1) {
					textCurrentIndexName.setText(availableStandards[indexSelectedStandard]);
				} else {
					textCurrentIndexName.setText("");
				}
				/*
				 * Set the first selection.
				 */
				IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
				List<? extends IPeak> peaks = chromatogram.getPeaks();
				peakTableViewerUI.setInput(peaks);
				peakTableViewerUI.getTable().setSelection(0);
				//
				if(!peaks.isEmpty()) {
					IPeak peak = peaks.get(0);
					Set<IIdentificationTarget> targets = peak.getTargets();
					targetsViewerUI.setInput(targets);
					targetsViewerUI.getTable().setSelection(0);
				}
			} else {
				peakTableViewerUI.setInput(null);
				textCurrentIndexName.setText("");
			}
			//
			updateLabel();
			validateSelection();
		}
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		//
		createRetentionIndexField(composite);
		createAutoAssignField(composite);
		createPeakTableField(composite);
		createTargetSpinnerField(composite);
		createAssignIndexField(composite);
		createPeakTargetsField(composite);
		//
		validateSelection();
		setControl(composite);
	}

	private void createRetentionIndexField(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridDataComposite = new GridData(GridData.FILL_HORIZONTAL);
		gridDataComposite.grabExcessHorizontalSpace = true;
		gridDataComposite.horizontalSpan = 3;
		composite.setLayoutData(gridDataComposite);
		composite.setLayout(new GridLayout(2, true));
		//
		Combo comboStartIndex = createComboStartIndex(composite);
		Combo comboStopIndex = createComboStopIndex(composite);
		//
		labelIndexRange = createLabelIndexRange(composite);
		/*
		 * Auto-Complete
		 */
		enableAutoComplete(comboStartIndex);
		enableAutoComplete(comboStopIndex);
	}

	private Combo createComboStartIndex(Composite parent) {

		Combo combo = EnhancedCombo.create(parent, SWT.NONE);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.setItems(availableStandards);
		combo.setToolTipText("Start Index");
		//
		combo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {

				setStartIndexName(combo);
			}
		});
		//
		return combo;
	}

	private void setStartIndexName(Combo combo) {

		wizardElements.setStartIndexName(combo.getText().trim());
		validateIndices();
	}

	private Combo createComboStopIndex(Composite parent) {

		Combo combo = EnhancedCombo.create(parent, SWT.NONE);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.setItems(availableStandards);
		combo.setToolTipText("Stop Index");
		//
		combo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {

				setStopIndexName(combo);
			}
		});
		//
		return combo;
	}

	private void setStopIndexName(Combo combo) {

		wizardElements.setStopIndexName(combo.getText().trim());
		validateIndices();
	}

	private Label createLabelIndexRange(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("");
		GridData gridDataLabel = new GridData(GridData.FILL_HORIZONTAL);
		gridDataLabel.grabExcessHorizontalSpace = true;
		gridDataLabel.horizontalSpan = 2;
		label.setLayoutData(gridDataLabel);
		//
		return label;
	}

	private void createAutoAssignField(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Auto Assign Standards");
		button.setToolTipText("Automatically assign the selected index range.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImageProvider.SIZE_16x16));
		//
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		button.setLayoutData(gridData);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.YES | SWT.NO | SWT.ICON_WARNING);
				messageBox.setText("Auto assign standards");
				messageBox.setMessage("Would you like to set all standards automatically?");
				if(messageBox.open() == SWT.YES) {
					IChromatogramSelection<?, ?> chromatogramSelection = wizardElements.getChromatogramSelection();
					if(chromatogramSelection != null && chromatogramSelection.getChromatogram() != null) {
						IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
						List<? extends IPeak> chromatogramPeaks = chromatogram.getPeaks();
						List<String> selectedIndices = wizardElements.getSelectedIndices();
						//
						if(chromatogramPeaks.size() == selectedIndices.size()) {
							for(int i = 0; i < chromatogramPeaks.size(); i++) {
								IPeak chromatogramPeak = chromatogramPeaks.get(i);
								setPeakTarget(chromatogramPeak, selectedIndices.get(i), true);
							}
							/*
							 * Set the first peak.
							 */
							peakTableViewerUI.getTable().setSelection(0);
							chromatogramSelection.reset();
							IPeak selectedPeak = getSelectedPeak();
							if(selectedPeak != null) {
								targetsViewerUI.setInput(selectedPeak.getTargets());
								targetsViewerUI.getTable().setSelection(0);
							}
						} else {
							String message = "The number of peaks (" + chromatogramPeaks.size() + ") and selected standards (" + selectedIndices.size() + ") is unequal.";
							updateStatus(message);
						}
					}
				}
			}
		});
	}

	private void createPeakTableField(Composite parent) {

		peakTableViewerUI = new PeakTableRetentionIndexViewerUI(parent, SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = 100;
		peakTableViewerUI.getTable().setLayoutData(gridData);
		peakTableViewerUI.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				IPeak selectedPeak = getSelectedPeak();
				if(selectedPeak != null) {
					targetsViewerUI.setInput(selectedPeak.getTargets());
					targetsViewerUI.getTable().setSelection(0);
				}
			}
		});
	}

	private void createTargetSpinnerField(Composite parent) {

		buttonPrevious = new Button(parent, SWT.PUSH);
		buttonPrevious.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PREVIOUS, IApplicationImageProvider.SIZE_16x16));
		buttonPrevious.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setCurrentIndexName(ACTION_DECREASE_INDEX);
			}
		});
		//
		textCurrentIndexName = new Text(parent, SWT.BORDER);
		textCurrentIndexName.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		textCurrentIndexName.setLayoutData(gridData);
		//
		buttonNext = new Button(parent, SWT.PUSH);
		buttonNext.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_NEXT, IApplicationImageProvider.SIZE_16x16));
		buttonNext.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setCurrentIndexName(ACTION_INCREASE_INDEX);
			}
		});
	}

	private void createAssignIndexField(Composite parent) {

		Button buttonAdd = new Button(parent, SWT.PUSH);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		buttonAdd.setLayoutData(gridData);
		buttonAdd.setText("Replace peak targets by selected index");
		buttonAdd.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE_ADD, IApplicationImageProvider.SIZE_16x16));
		buttonAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPeak selectedPeak = getSelectedPeak();
				if(selectedPeak != null) {
					String name = textCurrentIndexName.getText().trim();
					setPeakTarget(selectedPeak, name, true);
				}
			}
		});
	}

	private void setPeakTarget(IPeak peak, String name, boolean deleteOtherTargets) {

		/*
		 * Delete all other targets.
		 */
		if(deleteOtherTargets) {
			peak.getTargets().clear();
		}
		//
		try {
			float factor = 100.0f;
			IPeakLibraryInformation libraryInformation = new PeakLibraryInformation();
			libraryInformation.setName(name);
			libraryInformation.setDatabase(databaseName); // Important, otherwise LibraryService fails.
			IPeakComparisonResult comparisonResult = new PeakComparisonResult(factor, factor, factor, factor, factor);
			IIdentificationTarget peakTarget = new IdentificationTarget(libraryInformation, comparisonResult);
			peakTarget.setIdentifier(AlkaneIdentifier.IDENTIFIER);
			peak.getTargets().add(peakTarget);
			targetsViewerUI.setInput(peak.getTargets());
			/*
			 * Go to the next index.
			 */
			setCurrentIndexName(ACTION_INCREASE_INDEX);
		} catch(ReferenceMustNotBeNullException e1) {
			logger.warn(e1);
		}
	}

	private void createPeakTargetsField(Composite parent) {

		targetsViewerUI = new PeakTargetsViewerUI(parent, SWT.BORDER | SWT.MULTI);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = 100;
		targetsViewerUI.getTable().setLayoutData(gridData);
		targetsViewerUI.getControl().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == 127 && e.stateMask == 0) {
					/*
					 * Press "DEL" button.
					 */
					MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.YES | SWT.NO | SWT.ICON_WARNING);
					messageBox.setText("Delete identifications");
					messageBox.setMessage("Would you like to delete the identifications?");
					if(messageBox.open() == SWT.YES) {
						/*
						 * Delete the identifications.
						 */
						IPeak chromatogramPeakMSD = getSelectedPeak();
						if(chromatogramPeakMSD != null) {
							Table table = targetsViewerUI.getTable();
							int[] indices = table.getSelectionIndices();
							List<IIdentificationTarget> targetsToRemove = getPeakTargetList(table, indices);
							chromatogramPeakMSD.getTargets().removeAll(targetsToRemove);
							targetsViewerUI.setInput(chromatogramPeakMSD.getTargets());
							validateSelection();
						}
					}
				}
			}
		});
	}

	/*
	 * May return null.
	 */
	private IPeak getSelectedPeak() {

		Table table = peakTableViewerUI.getTable();
		int index = table.getSelectionIndex();
		Object object = peakTableViewerUI.getElementAt(index);
		if(object instanceof IPeak peak) {
			return peak;
		} else {
			return null;
		}
	}

	/*
	 * If no index is found, -1 will be returned.
	 */
	private int getStartIndex(String[] availableStandards, String startIndexName) {

		for(int i = 0; i < availableStandards.length; i++) {
			if(availableStandards[i].equals(startIndexName)) {
				return i;
			}
		}
		return -1;
	}

	private void setCurrentIndexName(int action) {

		switch(action) {
			case ACTION_INCREASE_INDEX:
				buttonPrevious.setEnabled(true);
				indexSelectedStandard++;
				if(indexSelectedStandard >= availableStandards.length) {
					indexSelectedStandard = availableStandards.length - 1;
					buttonNext.setEnabled(false);
				}
				break;
			case ACTION_DECREASE_INDEX:
				buttonNext.setEnabled(true);
				indexSelectedStandard--;
				if(indexSelectedStandard < 0) {
					indexSelectedStandard = 0;
					buttonPrevious.setEnabled(false);
				}
				break;
		}
		//
		textCurrentIndexName.setText(availableStandards[indexSelectedStandard]);
	}

	private List<IIdentificationTarget> getPeakTargetList(Table table, int[] indices) {

		List<IIdentificationTarget> targetList = new ArrayList<>();
		for(int index : indices) {
			/*
			 * Get the selected item.
			 */
			TableItem tableItem = table.getItem(index);
			Object object = tableItem.getData();
			if(object instanceof IIdentificationTarget target) {
				targetList.add(target);
			}
		}
		return targetList;
	}

	private void validateSelection() {

		String message = null;
		/*
		 * Updates the status
		 */
		updateStatus(message);
	}

	private void validateIndices() {

		validateIndexSelection();
		updateLabel();
	}

	private void updateLabel() {

		List<String> selectedIndices = wizardElements.getSelectedIndices();
		int size = wizardElements.getChromatogramSelection().getChromatogram().getPeaks().size();
		labelIndexRange.setText("Number of peaks (" + size + ") -> Selected Standards (" + selectedIndices.size() + ")");
	}

	private void validateIndexSelection() {

		String message = null;
		/*
		 * Start index
		 */
		String startIndexName = wizardElements.getStartIndexName();
		if(startIndexName.equals("")) {
			message = "Please select a start index.";
		} else {
			if(getComboIndex(startIndexName) == -1) {
				message = "The select start index is not valid.";
			}
		}
		/*
		 * Stop index
		 */
		if(message == null) {
			String stopIndexName = wizardElements.getStopIndexName();
			if(stopIndexName.equals("")) {
				message = "Please select a stop index.";
			} else {
				if(getComboIndex(stopIndexName) == -1) {
					message = "The select stop index is not valid.";
				}
			}
		}
		/*
		 * Updates the status
		 */
		updateStatus(message);
	}

	private int getComboIndex(String name) {

		for(int i = 0; i < availableStandards.length; i++) {
			if(availableStandards[i].equals(name)) {
				return i;
			}
		}
		return -1;
	}

	private void enableAutoComplete(Combo combo) {

		IContentProposalProvider proposalProvider = new IContentProposalProvider() {

			@Override
			public IContentProposal[] getProposals(String contents, int position) {

				List<ContentProposal> list = new ArrayList<>();
				if(contents != null) {
					String[] items = combo.getItems();
					for(String item : items) {
						if(item.toLowerCase().contains(contents.toLowerCase())) {
							list.add(new ContentProposal(item));
						}
					}
				}
				return list.toArray(new IContentProposal[0]);
			}
		};
		//
		autoComplete(combo, proposalProvider);
	}
}