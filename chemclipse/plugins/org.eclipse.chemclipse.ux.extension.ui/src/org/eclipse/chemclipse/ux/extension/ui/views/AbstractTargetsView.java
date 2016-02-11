/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.chromatogram.IChromatogramTargetMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IMassSpectrumTarget;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.ui.provider.TargetsLabelProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.TargetsTableComparator;
import org.eclipse.chemclipse.ux.extension.ui.provider.TargetsViewEditingSupport;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public abstract class AbstractTargetsView {

	private ExtendedTableViewer tableViewer;
	private TargetsTableComparator targetsTableComparator;
	private static final String VERIFIED_MANUALLY = "Verified (manually)";
	private String[] titles = {VERIFIED_MANUALLY, "Name", "CAS", "Match Factor", "Reverse Factor", "Formula", "Mol Weight", "Probability", "Advise", "Identifier", "Miscellaneous", "Comments", "Database", "Contributor"};
	private int bounds[] = {30, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
	private IStructuredContentProvider contentProvider;
	/*
	 * Event Broker
	 */
	private IEventBroker eventBroker;

	public AbstractTargetsView(IStructuredContentProvider contentProvider, IEventBroker eventBroker) {
		this.contentProvider = contentProvider;
		this.eventBroker = eventBroker;
	}

	public void createPartControl(Composite parent) {

		parent.setLayout(new FillLayout());
		/*
		 * Targets Table
		 */
		tableViewer = new ExtendedTableViewer(parent);
		tableViewer.createColumns(titles, bounds);
		tableViewer.setContentProvider(contentProvider);
		tableViewer.setLabelProvider(new TargetsLabelProvider());
		targetsTableComparator = new TargetsTableComparator();
		tableViewer.setComparator(targetsTableComparator);
		setEditingSupport();
		/*
		 * Copy and event broker actions.
		 */
		tableViewer.getControl().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == 99 && e.stateMask == 262144) {
					/*
					 * The selected content will be placed to the clipboard if
					 * the user is using "Function + c". "Function-Key" 262144
					 * (stateMask) + "c" 99 (keyCode)
					 */
					tableViewer.copyToClipboard(titles);
					//
				} else if(e.keyCode == 127 && e.stateMask == 0) {
					/*
					 * Press "DEL" button.
					 */
					deleteSelectedTargets();
				} else {
					propagateSelectedTarget();
				}
			}
		});
		tableViewer.getControl().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				propagateSelectedTarget();
			}
		});
		initContextMenu();
	}

	public TableViewer getTableViewer() {

		return tableViewer;
	}

	public void setFocus() {

		tableViewer.getControl().setFocus();
	}

	/**
	 * Updates the object. It could be a chromatogram, peak or mass spectrum.
	 * 
	 * @param object
	 * @param forceReload
	 */
	public void update(Object object, boolean forceReload) {

		/*
		 * Update the ui only if the actual view part is visible and the object
		 * is not null.
		 */
		if(object != null) {
			tableViewer.setInput(object);
			sortTableInitially(0, TargetsTableComparator.DESCENDING); // Manually Verified
		}
	}

	/**
	 * Sorts the targets table initially.
	 * 
	 * @param column
	 * @param sortOrder
	 */
	public void sortTableInitially(int column, int sortOrder) {

		targetsTableComparator.setColumn(column);
		targetsTableComparator.setDirection(sortOrder);
		tableViewer.refresh();
		targetsTableComparator.setDirection(1 - sortOrder); // toggle sort order
		targetsTableComparator.setColumn(0);
	}

	private void initContextMenu() {

		MenuManager menuManager = new MenuManager("#PopUpMenu", "org.eclipse.chemclipse.chromatogram.msd.ui.perspective.internal.views.targetsView.popup");
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
						tableViewer.copyToClipboard(titles);
					}
				};
				action.setText("Copy selection to clipboard");
				manager.add(action);
			}
		});
		/*
		 * Delete selected targets
		 */
		menuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {

				IAction action = new Action() {

					@Override
					public void run() {

						super.run();
						deleteSelectedTargets();
					}
				};
				action.setText("Delete selected targets");
				manager.add(action);
			}
		});
		Menu menu = menuManager.createContextMenu(tableViewer.getControl());
		tableViewer.getControl().setMenu(menu);
	}

	/**
	 * Propagates IUPAC name and CAS number of the selected element
	 * via the broker.
	 */
	private void propagateSelectedTarget() {

		Table table = tableViewer.getTable();
		int index = table.getSelectionIndex();
		if(index >= 0) {
			TableItem tableItem = table.getItem(index);
			Object object = tableItem.getData();
			if(object instanceof IIdentificationTarget) {
				IIdentificationTarget target = (IIdentificationTarget)object;
				eventBroker.send(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_UPDATE, target);
			}
		}
	}

	/*
	 * Delete the selected targets after confirming the message box.
	 */
	private void deleteSelectedTargets() {

		Shell shell = Display.getCurrent().getActiveShell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
		messageBox.setText("Delete Selected Targets");
		messageBox.setMessage("Do you really want to delete the selected targets?");
		int decision = messageBox.open();
		if(SWT.YES == decision) {
			/*
			 * Delete the selected items.
			 */
			Table table = tableViewer.getTable();
			int[] indices = table.getSelectionIndices();
			/*
			 * Delete the selected targets. Make a distinction between: -
			 * IChromatogram - IChromatogramPeak Don't delete entries in cause
			 * they are temporary: - IMassSpectrumIdentificationResult
			 */
			Object input = tableViewer.getInput();
			if(input instanceof IChromatogramMSD) {
				List<IChromatogramTargetMSD> targetsToRemove = getChromatogramTargetList(table, indices);
				IChromatogramMSD chromatogram = (IChromatogramMSD)input;
				chromatogram.removeTargets(targetsToRemove);
				//
			} else if(input instanceof IChromatogramPeakMSD) {
				List<IPeakTarget> targetsToRemove = getPeakTargetList(table, indices);
				IChromatogramPeakMSD chromatogramPeak = (IChromatogramPeakMSD)input;
				chromatogramPeak.removeTargets(targetsToRemove);
			} else if(input instanceof IScanMSD) {
				List<IMassSpectrumTarget> targetsToRemove = getMassSpectrumTargetList(table, indices);
				IScanMSD scan = (IScanMSD)input;
				scan.removeTargets(targetsToRemove);
			}
			/*
			 * Delete targets in table.
			 */
			table.remove(indices);
		}
	}

	private List<IMassSpectrumTarget> getMassSpectrumTargetList(Table table, int[] indices) {

		List<IMassSpectrumTarget> targetList = new ArrayList<IMassSpectrumTarget>();
		for(int index : indices) {
			/*
			 * Get the selected item.
			 */
			TableItem tableItem = table.getItem(index);
			Object object = tableItem.getData();
			if(object instanceof IMassSpectrumTarget) {
				IMassSpectrumTarget target = (IMassSpectrumTarget)object;
				targetList.add(target);
			}
		}
		return targetList;
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

	private List<IChromatogramTargetMSD> getChromatogramTargetList(Table table, int[] indices) {

		List<IChromatogramTargetMSD> targetList = new ArrayList<IChromatogramTargetMSD>();
		for(int index : indices) {
			/*
			 * Get the selected item.
			 */
			TableItem tableItem = table.getItem(index);
			Object object = tableItem.getData();
			if(object instanceof IChromatogramTargetMSD) {
				IChromatogramTargetMSD target = (IChromatogramTargetMSD)object;
				targetList.add(target);
			}
		}
		return targetList;
	}

	private void setEditingSupport() {

		TableViewer tableViewer = getTableViewer();
		List<TableViewerColumn> tableViewerColumns = this.tableViewer.getTableViewerColumns();
		for(int i = 0; i < tableViewerColumns.size(); i++) {
			TableViewerColumn tableViewerColumn = tableViewerColumns.get(i);
			String label = tableViewerColumn.getColumn().getText();
			if(label.equals(VERIFIED_MANUALLY)) {
				tableViewerColumn.setEditingSupport(new TargetsViewEditingSupport(tableViewer));
			}
		}
	}
}
