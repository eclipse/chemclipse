/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.csd.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.notifier.ChromatogramSelectionCSDUpdateNotifier;
import org.eclipse.chemclipse.csd.swt.ui.components.peak.PeakListUI;
import org.eclipse.chemclipse.model.comparator.PeakRetentionTimeComparator;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.implementation.Peaks;
import org.eclipse.chemclipse.model.selection.ChromatogramSelectionSupport;
import org.eclipse.chemclipse.model.selection.MoveDirection;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.ui.events.IKeyEventProcessor;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuCategories;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class PeakListCSDView extends AbstractChromatogramSelectionCSDView {

	private static final String POPUP_MENU_ID = "org.eclipse.chemclipse.chromatogram.csd.ui.perspective.views.peakListFIDView.popup";
	@Inject
	private Composite parent;
	private PeakListUI peakListUI;
	private IChromatogramPeakCSD firstPeak = null;
	private double firstIntegratedArea = 0.0d;
	private IChromatogramPeakCSD lastPeak = null;
	private double lastIntegratedArea = 0.0d;
	private IChromatogramSelectionCSD chromatogramSelectionMSDFocused;
	private PeakRetentionTimeComparator chromatogramPeakComparator;
	/*
	 * Update the cache if the peaks have been deleted.
	 */
	private boolean peakDeleteUpdate = false;

	@Inject
	public PeakListCSDView(EPartService partService, MPart part, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
		chromatogramPeakComparator = new PeakRetentionTimeComparator(SortOrder.ASC);
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		peakListUI = new PeakListUI(parent, SWT.NONE);
		final ExtendedTableViewer tableViewer = peakListUI.getTableViewer();
		ITableSettings tableSettings = tableViewer.getTableSettings();
		tableSettings.addKeyEventProcessor(new IKeyEventProcessor() {

			@Override
			public void handleEvent(ExtendedTableViewer extendedTableViewer, KeyEvent e) {

				if(e.keyCode == 127 && e.stateMask == 0) {
					/*
					 * Press "DEL" button.
					 */
					deleteSelectedPeaks();
				}
			}
		});
		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Delete Selected Peaks";
			}

			@Override
			public String getCategory() {

				return ITableMenuCategories.STANDARD_OPERATION;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				deleteSelectedPeaks();
			}
		});
		tableViewer.applySettings(tableSettings);
		/*
		 * Add a selection listener, to update peaks on click.
		 */
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				/*
				 * Is the selection a structured selection?
				 */
				if(event != null) {
					ISelection selection = event.getSelection();
					if(selection instanceof IStructuredSelection) {
						IStructuredSelection structuredSelection = (IStructuredSelection)selection;
						Object element = structuredSelection.getFirstElement();
						/*
						 * Is the element an instance of IPeak?
						 */
						if(element instanceof IChromatogramPeakCSD) {
							/*
							 * Is the chromatogram updatable?
							 * IChromatogramSelection at itself isn't.
							 */
							IChromatogramSelectionCSD chromatogramSelection = getChromatogramSelection();
							if(chromatogramSelection instanceof ChromatogramSelectionCSD) {
								Display display = Display.getDefault();
								try {
									/*
									 * Use the wait cursor.
									 */
									if(display != null) {
										display.getCursorControl().setCursor(display.getSystemCursor(SWT.CURSOR_WAIT));
									}
									/*
									 * Show the selected peak.
									 */
									IChromatogramPeakCSD selectedPeak = (IChromatogramPeakCSD)element;
									((ChromatogramSelectionCSD)chromatogramSelection).setSelectedPeak(selectedPeak, false);
									if(PreferenceSupplier.isMoveRetentionTimeOnPeakSelection()) {
										adjustChromatogramSelection(selectedPeak, chromatogramSelection);
									}
									ChromatogramSelectionCSDUpdateNotifier.fireUpdateChange(chromatogramSelection, true);
								} finally {
									/*
									 * Set the arrow cursor.
									 */
									if(display != null) {
										display.getCursorControl().setCursor(display.getSystemCursor(SWT.CURSOR_ARROW));
									}
								}
							}
							chromatogramSelection = null;
						}
					}
				}
			}
		});
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		peakListUI.getTableViewer().getControl().setFocus();
		update(getChromatogramSelection(), false);
	}

	@Override
	public void update(IChromatogramSelectionCSD chromatogramSelection, boolean forceReload) {

		/*
		 * Update the ui only if the actual view part is visible and the
		 * selection is not null.
		 */
		if(doUpdate(chromatogramSelection)) {
			/*
			 * Check if the chromatogram selection is actually selected.
			 */
			if(chromatogramSelectionMSDFocused == null || chromatogramSelectionMSDFocused != chromatogramSelection) {
				/*
				 * No: Load the selection
				 */
				chromatogramSelectionMSDFocused = chromatogramSelection;
				List<IChromatogramPeakCSD> peakList = chromatogramSelection.getChromatogramCSD().getPeaks(chromatogramSelection);
				IPeaks peaks = getPeaks(peakList);
				peakListUI.update(peaks, forceReload);
			} else {
				/*
				 * Yes: Reload the selection
				 */
				updatePeaksInList(chromatogramSelection, forceReload);
			}
		} else {
			if(chromatogramSelection == null) {
				/*
				 * Remove the reference.
				 */
				chromatogramSelectionMSDFocused = null;
				if(isPartVisible()) {
					/*
					 * Clear the list.
					 */
					peakListUI.clear();
				}
			}
		}
	}

	/*
	 * Delete the selected peaks after confirming the message box.
	 */
	private void deleteSelectedPeaks() {

		Shell shell = Display.getCurrent().getActiveShell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
		messageBox.setText("Delete Selected Peaks");
		messageBox.setMessage("Do you really want to delete the selected peaks?");
		int decision = messageBox.open();
		if(SWT.YES == decision) {
			/*
			 * Delete
			 */
			peakDeleteUpdate = true;
			peakListUI.deleteSelectedPeaks(getChromatogramSelection());
		}
	}

	private void updatePeaksInList(IChromatogramSelectionCSD chromatogramSelection, boolean forceReload) {

		/*
		 * Don't reload the peak list if it still has been loaded.
		 * Take care, if there is none or only one peak selected!
		 */
		List<IChromatogramPeakCSD> peakList = chromatogramSelection.getChromatogramCSD().getPeaks(chromatogramSelection);
		int size = peakList.size();
		if(size > 0) {
			if(size == 1) {
				checkFirstPeakUpdate(peakList, forceReload);
			} else {
				checkFirstAndLastPeakUpdate(peakList, forceReload);
			}
		} else {
			peakListUI.clear();
			firstPeak = null;
			firstIntegratedArea = 0.0d;
			lastPeak = null;
			lastIntegratedArea = 0.0d;
		}
	}

	private void checkFirstPeakUpdate(List<IChromatogramPeakCSD> peakList, boolean forceReload) {

		/*
		 * Check the first peak only.
		 */
		IChromatogramPeakCSD peak = peakList.get(0);
		if(firstPeak != peak || peakDeleteUpdate) {
			/*
			 * Don't access the method twice on delete action.
			 */
			peakDeleteUpdate = false;
			//
			firstPeak = peak;
			firstIntegratedArea = peak.getIntegratedArea();
			IPeaks peaks = getPeaks(peakList);
			peakListUI.update(peaks, forceReload);
		} else {
			/*
			 * Update if an integrator has been applied.
			 */
			if(reloadList(false)) {
				peakListUI.getTableViewer().refresh();
			}
		}
	}

	private void checkFirstAndLastPeakUpdate(List<IChromatogramPeakCSD> peakList, boolean forceReload) {

		/*
		 * Check the first and last peak.
		 */
		int size = peakList.size();
		IChromatogramPeakCSD peakOne = peakList.get(0);
		IChromatogramPeakCSD peakTwo = peakList.get(size - 1);
		//
		if(firstPeak != peakOne || lastPeak != peakTwo || peakDeleteUpdate) {
			/*
			 * Don't access the method twice on delete action.
			 */
			peakDeleteUpdate = false;
			//
			firstPeak = peakOne;
			firstIntegratedArea = peakOne.getIntegratedArea();
			lastPeak = peakTwo;
			lastIntegratedArea = peakTwo.getIntegratedArea();
			IPeaks peaks = getPeaks(peakList);
			peakListUI.update(peaks, forceReload);
		} else {
			if(reloadList(true)) {
				peakListUI.getTableViewer().refresh();
			}
		}
	}

	private IPeaks getPeaks(List<IChromatogramPeakCSD> peakList) {

		IPeaks peaks = new Peaks();
		for(IChromatogramPeakCSD peak : peakList) {
			peaks.addPeak(peak);
		}
		return peaks;
	}

	private boolean reloadList(boolean checkSecondPeak) {

		if(firstPeak != null && firstPeak.getIntegratedArea() != firstIntegratedArea) {
			return true;
		}
		if(checkSecondPeak && lastPeak != null && lastPeak.getIntegratedArea() != lastIntegratedArea) {
			return true;
		}
		return false;
	}

	private void adjustChromatogramSelection(IChromatogramPeakCSD selectedPeak, IChromatogramSelectionCSD chromatogramSelection) {

		/*
		 * TODO refactor Editor CSD, MSD, WSD and peak list view!
		 */
		IChromatogramCSD chromatogramCSD = chromatogramSelection.getChromatogramCSD();
		List<IChromatogramPeakCSD> peaks = new ArrayList<>(chromatogramCSD.getPeaks());
		List<IChromatogramPeakCSD> peaksSelection = new ArrayList<>(chromatogramCSD.getPeaks(chromatogramSelection));
		Collections.sort(peaks, chromatogramPeakComparator);
		Collections.sort(peaksSelection, chromatogramPeakComparator);
		//
		if(peaks.get(0).equals(selectedPeak) || peaks.get(peaks.size() - 1).equals(selectedPeak)) {
			/*
			 * Don't move if it is the first or last peak of the chromatogram.
			 */
		} else {
			/*
			 * First peak of the selection: move left
			 * Last peak of the selection: move right
			 */
			if(peaksSelection.get(0).equals(selectedPeak)) {
				ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, MoveDirection.LEFT, 5);
			} else if(peaksSelection.get(peaksSelection.size() - 1).equals(selectedPeak)) {
				ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, MoveDirection.RIGHT, 5);
			}
		}
	}
}