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
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.library.LibraryService;
import org.eclipse.chemclipse.chromatogram.msd.identifier.processing.ILibraryServiceProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl.AlkaneIdentifier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.IPeakComparisonResult;
import org.eclipse.chemclipse.model.identifier.IPeakLibraryInformation;
import org.eclipse.chemclipse.model.identifier.PeakComparisonResult;
import org.eclipse.chemclipse.model.identifier.PeakLibraryInformation;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.model.targets.PeakTarget;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassSpectrumComparisonUI;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassValueDisplayPrecision;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.wizards.AbstractExtendedWizardPage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.PeakTableRetentionIndexViewerUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.PeakTargetsViewerUI;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class PagePeakAssignmentMSD extends AbstractExtendedWizardPage {

	private static final Logger logger = Logger.getLogger(PagePeakAssignmentMSD.class);
	//
	private IRetentionIndexWizardElements wizardElements;
	private PeakTableRetentionIndexViewerUI peakTableViewerUI;
	private MassSpectrumComparisonUI libraryMassSpectrumComparisonUI;
	private PeakTargetsViewerUI targetsViewerUI;
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

	public PagePeakAssignmentMSD(IRetentionIndexWizardElements wizardElements) {
		//
		super(PagePeakAssignmentMSD.class.getName());
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
			IChromatogramSelectionMSD chromatogramSelectionMSD = wizardElements.getChromatogramSelectionMSD();
			if(chromatogramSelectionMSD != null && chromatogramSelectionMSD.getChromatogramMSD() != null) {
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
				IChromatogramMSD chromatogramMSD = chromatogramSelectionMSD.getChromatogramMSD();
				List<IChromatogramPeakMSD> peaks = chromatogramMSD.getPeaks();
				peakTableViewerUI.setInput(peaks);
				peakTableViewerUI.getTable().setSelection(0);
				//
				if(peaks.size() > 0) {
					IChromatogramPeakMSD peak = peaks.get(0);
					List<IPeakTarget> targets = peak.getTargets();
					targetsViewerUI.setInput(targets);
					targetsViewerUI.getTable().setSelection(0);
					//
					showLibraryMassSpectrum();
				}
			} else {
				peakTableViewerUI.setInput(null);
				textCurrentIndexName.setText("");
			}
			validateSelection();
		}
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		//
		createAutoAssignField(composite);
		createPeakTableField(composite);
		createLibraryComparisonField(composite);
		createTargetSpinnerField(composite);
		createAssignIndexField(composite);
		createPeakTargetsField(composite);
		//
		validateSelection();
		setControl(composite);
	}

	private void createAutoAssignField(Composite composite) {

		Button button = new Button(composite, SWT.PUSH);
		button.setText("Auto Assign Standards");
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
					IChromatogramSelectionMSD chromatogramSelectionMSD = wizardElements.getChromatogramSelectionMSD();
					if(chromatogramSelectionMSD != null && chromatogramSelectionMSD.getChromatogramMSD() != null) {
						IChromatogramMSD chromatogramMSD = chromatogramSelectionMSD.getChromatogramMSD();
						List<IChromatogramPeakMSD> chromatogramPeaks = chromatogramMSD.getPeaks();
						List<IRetentionIndexEntry> retentionIndexEntries = wizardElements.getSelectedRetentionIndexEntries();
						//
						if(chromatogramPeaks.size() == retentionIndexEntries.size()) {
							for(int i = 0; i < chromatogramPeaks.size(); i++) {
								IChromatogramPeakMSD chromatogramPeak = chromatogramPeaks.get(i);
								IRetentionIndexEntry retentionIndexEntry = retentionIndexEntries.get(i);
								setPeakTarget(chromatogramPeak, retentionIndexEntry.getName(), true);
							}
							/*
							 * Set the first peak.
							 */
							peakTableViewerUI.getTable().setSelection(0);
							chromatogramSelectionMSD.reset();
							IChromatogramPeakMSD selectedPeak = getSelectedPeak();
							if(selectedPeak != null) {
								targetsViewerUI.setInput(selectedPeak.getTargets());
								targetsViewerUI.getTable().setSelection(0);
								showLibraryMassSpectrum();
							}
						} else {
							String message = "The number of peaks (" + chromatogramPeaks.size() + ") and selected standards (" + retentionIndexEntries.size() + ") is unequal.";
							updateStatus(message);
						}
					}
				}
			}
		});
	}

	private void createPeakTableField(Composite composite) {

		peakTableViewerUI = new PeakTableRetentionIndexViewerUI(composite, SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = 100;
		peakTableViewerUI.getTable().setLayoutData(gridData);
		peakTableViewerUI.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				IChromatogramPeakMSD selectedPeak = getSelectedPeak();
				if(selectedPeak != null) {
					targetsViewerUI.setInput(selectedPeak.getTargets());
					targetsViewerUI.getTable().setSelection(0);
					showLibraryMassSpectrum();
				}
			}
		});
	}

	private void createLibraryComparisonField(Composite composite) {

		Composite parent = new Composite(composite, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 3;
		parent.setLayoutData(gridData);
		parent.setLayout(new FillLayout());
		libraryMassSpectrumComparisonUI = new MassSpectrumComparisonUI(parent, SWT.NONE, MassValueDisplayPrecision.NOMINAL, "UNKNOWN", "LIBRARY");
	}

	private void createTargetSpinnerField(Composite composite) {

		buttonPrevious = new Button(composite, SWT.PUSH);
		buttonPrevious.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PREVIOUS, IApplicationImage.SIZE_16x16));
		buttonPrevious.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setCurrentIndexName(ACTION_DECREASE_INDEX);
			}
		});
		//
		textCurrentIndexName = new Text(composite, SWT.BORDER);
		textCurrentIndexName.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		textCurrentIndexName.setLayoutData(gridData);
		//
		buttonNext = new Button(composite, SWT.PUSH);
		buttonNext.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_NEXT, IApplicationImage.SIZE_16x16));
		buttonNext.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setCurrentIndexName(ACTION_INCREASE_INDEX);
			}
		});
	}

	private void createAssignIndexField(Composite composite) {

		Button buttonAdd = new Button(composite, SWT.PUSH);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		buttonAdd.setLayoutData(gridData);
		buttonAdd.setText("Replace peak targets by selected index");
		buttonAdd.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE_ADD, IApplicationImage.SIZE_16x16));
		buttonAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IChromatogramPeakMSD selectedPeak = getSelectedPeak();
				if(selectedPeak != null) {
					String name = textCurrentIndexName.getText().trim();
					setPeakTarget(selectedPeak, name, true);
				}
			}
		});
	}

	private void setPeakTarget(IChromatogramPeakMSD chromatogramPeak, String name, boolean deleteOtherTargets) {

		/*
		 * Delete all other targets.
		 */
		if(deleteOtherTargets) {
			chromatogramPeak.removeAllTargets();
		}
		//
		try {
			float FACTOR = 100.0f;
			IPeakLibraryInformation libraryInformation = new PeakLibraryInformation();
			libraryInformation.setName(name);
			libraryInformation.setDatabase(databaseName); // Important, otherwise LibraryService fails.
			IPeakComparisonResult comparisonResult = new PeakComparisonResult(FACTOR, FACTOR, FACTOR, FACTOR, FACTOR);
			IPeakTarget peakTarget = new PeakTarget(libraryInformation, comparisonResult);
			peakTarget.setIdentifier(AlkaneIdentifier.IDENTIFIER);
			chromatogramPeak.addTarget(peakTarget);
			targetsViewerUI.setInput(chromatogramPeak.getTargets());
			/*
			 * Go to the next index.
			 */
			setCurrentIndexName(ACTION_INCREASE_INDEX);
		} catch(ReferenceMustNotBeNullException e1) {
			logger.warn(e1);
		}
	}

	private void createPeakTargetsField(Composite composite) {

		targetsViewerUI = new PeakTargetsViewerUI(composite, SWT.BORDER | SWT.MULTI);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = 100;
		targetsViewerUI.getTable().setLayoutData(gridData);
		targetsViewerUI.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				showLibraryMassSpectrum();
			}
		});
		//
		targetsViewerUI.getControl().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == 127 && e.stateMask == 0) {
					/*
					 * Press "DEL" button.
					 */
					MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.YES | SWT.NO | SWT.ICON_WARNING);
					messageBox.setText("Delete identification(s)");
					messageBox.setMessage("Would you like to delete the identification(s)?");
					if(messageBox.open() == SWT.YES) {
						/*
						 * Delete the identifications.
						 */
						IChromatogramPeakMSD chromatogramPeakMSD = getSelectedPeak();
						if(chromatogramPeakMSD != null) {
							Table table = targetsViewerUI.getTable();
							int[] indices = table.getSelectionIndices();
							List<IPeakTarget> targetsToRemove = getPeakTargetList(table, indices);
							chromatogramPeakMSD.removeTargets(targetsToRemove);
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
	private IChromatogramPeakMSD getSelectedPeak() {

		Table table = peakTableViewerUI.getTable();
		int index = table.getSelectionIndex();
		Object object = peakTableViewerUI.getElementAt(index);
		if(object instanceof IChromatogramPeakMSD) {
			return (IChromatogramPeakMSD)object;
		} else {
			return null;
		}
	}

	/*
	 * May return null.
	 */
	private IPeakTarget getSelectedTarget() {

		Table table = targetsViewerUI.getTable();
		int index = table.getSelectionIndex();
		Object object = targetsViewerUI.getElementAt(index);
		if(object instanceof IPeakTarget) {
			return (IPeakTarget)object;
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

	private void showLibraryMassSpectrum() {

		IPeakTarget peakTarget = getSelectedTarget();
		if(peakTarget != null) {
			/*
			 * Get the selected peak
			 */
			IChromatogramPeakMSD selectedPeak = getSelectedPeak();
			if(selectedPeak != null) {
				/*
				 * Get the target.
				 */
				ILibraryServiceProcessingInfo processingInfo = LibraryService.identify(peakTarget, new NullProgressMonitor());
				try {
					IMassSpectra massSpectra = processingInfo.getMassSpectra();
					if(massSpectra.size() > 0) {
						/*
						 * Show the mass spectrum in comparison modus.
						 */
						IScanMSD unknownMassSpectrum = selectedPeak.getPeakModel().getPeakMassSpectrum();
						IScanMSD libraryMassSpectrum = massSpectra.getMassSpectrum(1);
						libraryMassSpectrumComparisonUI.update(unknownMassSpectrum, libraryMassSpectrum, true);
					}
				} catch(Exception e) {
					logger.warn(e);
				}
			}
		}
	}

	private List<IPeakTarget> getPeakTargetList(Table table, int[] indices) {

		List<IPeakTarget> targetList = new ArrayList<IPeakTarget>();
		for(int index : indices) {
			/*
			 * Get the selected item.
			 */
			TableItem tableItem = table.getItem(index);
			Object object = tableItem.getData();
			if(object instanceof IPeakTarget) {
				IPeakTarget target = (IPeakTarget)object;
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
}
