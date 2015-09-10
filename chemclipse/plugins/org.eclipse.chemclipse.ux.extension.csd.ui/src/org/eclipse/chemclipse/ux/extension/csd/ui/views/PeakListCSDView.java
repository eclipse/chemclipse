/*******************************************************************************
 * Copyright (c) 2014, 2015 Dr. Philip Wenig.
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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

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
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.notifier.ChromatogramSelectionCSDUpdateNotifier;
import org.eclipse.chemclipse.csd.swt.ui.components.peak.PeakListUI;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.implementation.Peaks;
import org.eclipse.chemclipse.support.ui.swt.viewers.ExtendedTableViewer;

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
	/*
	 * Update the cache if the peaks have been deleted.
	 */
	private boolean peakDeleteUpdate = false;

	@Inject
	public PeakListCSDView(EPartService partService, MPart part, IEventBroker eventBroker) {

		super(part, partService, eventBroker);
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		peakListUI = new PeakListUI(parent, SWT.NONE);
		final ExtendedTableViewer tableViewer = peakListUI.getTableViewer();
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
									((ChromatogramSelectionCSD)chromatogramSelection).setSelectedPeak((IChromatogramPeakCSD)element, false);
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
					tableViewer.copyToClipboard(peakListUI.getTitles());
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
						peakListUI.getTableViewer().copyToClipboard(peakListUI.getTitles());
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
		TableViewer tableViewer = peakListUI.getTableViewer();
		Menu menu = menuManager.createContextMenu(tableViewer.getTable());
		tableViewer.getTable().setMenu(menu);
	}
}