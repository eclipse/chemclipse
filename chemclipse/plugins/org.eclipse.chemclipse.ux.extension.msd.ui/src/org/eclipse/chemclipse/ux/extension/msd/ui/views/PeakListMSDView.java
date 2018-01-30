/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.model.comparator.PeakRetentionTimeComparator;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.implementation.Peaks;
import org.eclipse.chemclipse.model.selection.ChromatogramSelectionSupport;
import org.eclipse.chemclipse.model.selection.MoveDirection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.notifier.ChromatogramSelectionMSDUpdateNotifier;
import org.eclipse.chemclipse.msd.swt.ui.components.peak.PeakListUI;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class PeakListMSDView extends AbstractChromatogramSelectionMSDView {

	private static final String POPUP_MENU_ID = "org.eclipse.chemclipse.chromatogram.msd.ui.perspective.views.peakListMSDView.popup";
	@Inject
	private Composite parent;
	private PeakListUI peakListUI;
	private IChromatogramPeakMSD firstPeak = null;
	private double firstIntegratedArea = 0.0d;
	private IChromatogramPeakMSD lastPeak = null;
	private double lastIntegratedArea = 0.0d;
	private float retentionIndexSum = 0.0f; // initial == 0
	private IChromatogramSelectionMSD chromatogramSelectionMSDFocused;
	private PeakRetentionTimeComparator chromatogramPeakComparator;
	/*
	 * Update the cache if the peaks have been deleted.
	 */
	private boolean peakDeleteUpdate = false;

	@Inject
	public PeakListMSDView(EPartService partService, MPart part, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
		chromatogramPeakComparator = new PeakRetentionTimeComparator(SortOrder.ASC);
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		peakListUI = new PeakListUI(parent, SWT.NONE);
		TableViewer tableViewer = peakListUI.getTableViewer();
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
						if(element instanceof IChromatogramPeakMSD) {
							/*
							 * Is the chromatogram updatable?
							 * IChromatogramSelection at itself isn't.
							 */
							IChromatogramSelectionMSD chromatogramSelection = getChromatogramSelection();
							if(chromatogramSelection instanceof ChromatogramSelectionMSD) {
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
									IChromatogramPeakMSD selectedPeak = (IChromatogramPeakMSD)element;
									((ChromatogramSelectionMSD)chromatogramSelection).setSelectedPeak(selectedPeak, false);
									if(PreferenceSupplier.isMoveRetentionTimeOnPeakSelection()) {
										adjustChromatogramSelection(selectedPeak, chromatogramSelection);
									}
									ChromatogramSelectionMSDUpdateNotifier.fireUpdateChange(chromatogramSelection, true);
								} finally {
									/*
									 * Set the arrow cursor.
									 */
									if(display != null && display.getCursorControl() != null) {
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
		/*
		 * Copy and Paste of the table content.
		 */
		tableViewer.getTable().addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == 99 && e.stateMask == 262144) {
					/*
					 * The selected content will be placed to the clipboard if
					 * the user is using "Function + c". "Function-Key" 262144
					 * (stateMask) + "c" 99 (keyCode)
					 */
					// peakListUI.getTableViewer().copyToClipboard(peakListUI.getTitles());
					//
				} else if(e.keyCode == 127 && e.stateMask == 0) {
					/*
					 * Press "DEL" button. The selected peaks will be deleted.
					 */
					deleteSelectedPeaks();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
		initContextMenu();
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
	public void update(IChromatogramSelectionMSD chromatogramSelection, boolean forceReload) {

		/*
		 * Update the ui only if the actual view part is visible and the
		 * selection is not null.
		 */
		if(doUpdate(chromatogramSelection)) {
			/*
			 * Display the information of the selected peak.
			 */
			IChromatogramPeakMSD selectedPeak = chromatogramSelection.getSelectedPeak();
			peakListUI.setLabelSelectedPeak(selectedPeak);
			/*
			 * Check if the chromatogram selection is actually selected.
			 */
			peakListUI.setChromatogramSelection(chromatogramSelection);
			if(chromatogramSelectionMSDFocused == null || chromatogramSelectionMSDFocused != chromatogramSelection) {
				/*
				 * No: Load the selection
				 */
				chromatogramSelectionMSDFocused = chromatogramSelection;
				List<IChromatogramPeakMSD> peakList = chromatogramSelection.getChromatogramMSD().getPeaks(chromatogramSelection);
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

	private void deactivateSelectedPeaks() {

		peakListUI.setActiveStatusSelectedPeaks(getChromatogramSelection(), false);
	}

	private void activateSelectedPeaks() {

		peakListUI.setActiveStatusSelectedPeaks(getChromatogramSelection(), true);
	}

	private void exportSelectedPeaks() {

		peakListUI.exportSelectedPeaks(getChromatogramSelection());
	}

	private void updatePeaksInList(IChromatogramSelectionMSD chromatogramSelection, boolean forceReload) {

		/*
		 * Don't reload the peak list if it still has been loaded. Take care, if
		 * there is none or only one peak selected!
		 */
		List<IChromatogramPeakMSD> peakList = chromatogramSelection.getChromatogramMSD().getPeaks(chromatogramSelection);
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
			retentionIndexSum = 0.0f;
		}
	}

	private void checkFirstPeakUpdate(List<IChromatogramPeakMSD> peakList, boolean forceReload) {

		/*
		 * Check the first peak only.
		 */
		IChromatogramPeakMSD peak = peakList.get(0);
		if(firstPeak != peak || peakDeleteUpdate) {
			/*
			 * Don't access the method twice on delete action.
			 */
			peakDeleteUpdate = false;
			//
			firstPeak = peak;
			firstIntegratedArea = peak.getIntegratedArea();
			retentionIndexSum = getSumRetentionIndices(peakList);
			IPeaks peaks = getPeaks(peakList);
			peakListUI.update(peaks, forceReload);
		} else {
			/*
			 * Update if an integrator has been applied.
			 */
			if(reloadList(false)) {
				peakListUI.getTableViewer().refresh();
			} else {
				if(forceReload) {
					/*
					 * Checking the retention indices could be too time
					 * consuming. But otherwise, changes of RI will be not
					 * detected.
					 */
					if(checkReloadRetetionIndices(peakList, forceReload)) {
						retentionIndexSum = getSumRetentionIndices(peakList);
						peakListUI.getTableViewer().refresh();
					}
				}
			}
		}
	}

	private void checkFirstAndLastPeakUpdate(List<IChromatogramPeakMSD> peakList, boolean forceReload) {

		/*
		 * Check the first and last peak.
		 */
		int size = peakList.size();
		IChromatogramPeakMSD peakOne = peakList.get(0);
		IChromatogramPeakMSD peakTwo = peakList.get(size - 1);
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
			retentionIndexSum = getSumRetentionIndices(peakList);
			IPeaks peaks = getPeaks(peakList);
			peakListUI.update(peaks, forceReload);
		} else {
			if(reloadList(true)) {
				peakListUI.getTableViewer().refresh();
			} else {
				if(forceReload) {
					/*
					 * Checking the retention indices could be too time
					 * consuming. But otherwise, changes of RI will be not
					 * detected.
					 */
					if(checkReloadRetetionIndices(peakList, forceReload)) {
						retentionIndexSum = getSumRetentionIndices(peakList);
						peakListUI.getTableViewer().refresh();
					}
				}
			}
		}
	}

	private boolean checkReloadRetetionIndices(List<IChromatogramPeakMSD> peakList, boolean forceReload) {

		/*
		 * Test
		 */
		if(retentionIndexSum != getSumRetentionIndices(peakList)) {
			return true;
		}
		return false;
	}

	private float getSumRetentionIndices(List<IChromatogramPeakMSD> peakList) {

		float sumRetentionIndices = 0.0f;
		for(IChromatogramPeakMSD peak : peakList) {
			sumRetentionIndices += peak.getPeakModel().getPeakMaximum().getRetentionIndex();
		}
		//
		return sumRetentionIndices;
	}

	private IPeaks getPeaks(List<IChromatogramPeakMSD> peakList) {

		IPeaks peaks = new Peaks();
		for(IChromatogramPeakMSD peak : peakList) {
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

	/*
	 * Initialize a context menu.
	 */
	private void initContextMenu() {

		MenuManager menuManager = new MenuManager("#PopUpMenu", POPUP_MENU_ID);
		menuManager.setRemoveAllWhenShown(true);
		/*
		 * Copy to clipboard
		 */
		menuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {

				IAction action = new Action() {

					@Override
					public void run() {

						super.run();
						// peakListUI.getTableViewer().copyToClipboard(peakListUI.getTitles());
					}
				};
				action.setText("Copy selection to clipboard");
				manager.add(action);
			}
		});
		/*
		 * Delete selected peaks
		 */
		menuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {

				IAction action = new Action() {

					@Override
					public void run() {

						super.run();
						deleteSelectedPeaks();
					}
				};
				action.setText("Delete the selected peak(s)");
				manager.add(action);
			}
		});
		/*
		 * Deactivate peaks
		 */
		menuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {

				IAction action = new Action() {

					@Override
					public void run() {

						super.run();
						deactivateSelectedPeaks();
					}
				};
				action.setText("Deactivate the selected peak(s)");
				manager.add(action);
			}
		});
		/*
		 * Activate selected peaks
		 */
		menuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {

				IAction action = new Action() {

					@Override
					public void run() {

						super.run();
						activateSelectedPeaks();
					}
				};
				action.setText("Activate the selected peak(s)");
				manager.add(action);
			}
		});
		/*
		 * Export selected peaks
		 */
		menuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {

				IAction action = new Action() {

					@Override
					public void run() {

						super.run();
						exportSelectedPeaks();
					}
				};
				action.setText("Export the selected peak(s)");
				manager.add(action);
			}
		});
		TableViewer tableViewer = peakListUI.getTableViewer();
		Menu menu = menuManager.createContextMenu(tableViewer.getTable());
		tableViewer.getTable().setMenu(menu);
	}

	private void adjustChromatogramSelection(IChromatogramPeakMSD selectedPeak, IChromatogramSelectionMSD chromatogramSelection) {

		/*
		 * TODO refactor Editor CSD, MSD, WSD and peak list view!
		 */
		IChromatogramMSD chromatogramMSD = chromatogramSelection.getChromatogramMSD();
		List<IChromatogramPeakMSD> peaks = new ArrayList<>(chromatogramMSD.getPeaks());
		List<IChromatogramPeakMSD> peaksSelection = new ArrayList<>(chromatogramMSD.getPeaks(chromatogramSelection));
		Collections.sort(peaks, chromatogramPeakComparator);
		Collections.sort(peaksSelection, chromatogramPeakComparator);
		//
		if(peaks.get(0).equals(selectedPeak) || peaks.get(peaks.size() - 1).equals(selectedPeak)) {
			/*
			 * Don't move if it is the first or last peak of the chromatogram.
			 */
		} else {
			/*
			 * First peak of the selection: move left Last peak of the
			 * selection: move right
			 */
			if(peaksSelection.get(0).equals(selectedPeak)) {
				ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, MoveDirection.LEFT, 5);
			} else if(peaksSelection.get(peaksSelection.size() - 1).equals(selectedPeak)) {
				ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, MoveDirection.RIGHT, 5);
			}
		}
	}
}