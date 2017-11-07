/*******************************************************************************
 * Copyright (c) 2012, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
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
import org.eclipse.chemclipse.support.ui.events.IKeyEventProcessor;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuCategories;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.chemclipse.ux.extension.ui.provider.TargetsLabelProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.TargetsTableComparator;
import org.eclipse.chemclipse.ux.extension.ui.provider.TargetsViewEditingSupport;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public abstract class AbstractTargetsView {

	private ExtendedTableViewer tableViewer;
	private TargetsTableComparator targetsTableComparator;
	private static final String VERIFIED_MANUALLY = "Verified (manually)";
	private String[] titles = {VERIFIED_MANUALLY, "Rating", "Name", "CAS", "Match Factor", "Reverse Factor", "Match Factor Direct", "Reverse Factor Direct", "Probability", "Formula", "SMILES", "InChI", "Mol Weight", "Advise", "Identifier", "Miscellaneous", "Comments", "Database", "Contributor", "Reference ID"};
	private int bounds[] = {30, 30, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
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
		ITableSettings tableSettings = tableViewer.getTableSettings();
		tableSettings.addKeyEventProcessor(new IKeyEventProcessor() {

			@Override
			public void handleEvent(ExtendedTableViewer extendedTableViewer, KeyEvent e) {

				if(e.keyCode == 127 && e.stateMask == 0) {
					/*
					 * Press "DEL" button.
					 */
					deleteSelectedTargets();
				} else if(e.keyCode == 105 && e.stateMask == 262144) {
					/*
					 * CTRL + I
					 */
					manuallyVerifySelectedTargets();
				} else {
					propagateSelectedTarget();
				}
			}
		});
		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Delete Selected Targets";
			}

			@Override
			public String getCategory() {

				return ITableMenuCategories.STANDARD_OPERATION;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				deleteSelectedTargets();
			}
		});
		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Manully Verify Selected Targets";
			}

			@Override
			public String getCategory() {

				return ITableMenuCategories.STANDARD_OPERATION;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				manuallyVerifySelectedTargets();
			}
		});
		tableViewer.applySettings(tableSettings);
		//
		tableViewer.getControl().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				propagateSelectedTarget();
			}
		});
		//
		targetsTableComparator = new TargetsTableComparator();
		tableViewer.setComparator(targetsTableComparator);
		setEditingSupport();
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
		targetsTableComparator.setColumn(column);
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

		MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.YES | SWT.NO | SWT.ICON_WARNING);
		messageBox.setText("Delete Selected Targets");
		messageBox.setMessage("Do you really want to delete the selected targets?");
		if(messageBox.open() == SWT.YES) {
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
				tableViewer.refresh();
				//
			} else if(input instanceof IChromatogramPeakMSD) {
				List<IPeakTarget> targetsToRemove = getPeakTargetList(table, indices);
				IChromatogramPeakMSD chromatogramPeak = (IChromatogramPeakMSD)input;
				chromatogramPeak.removeTargets(targetsToRemove);
				tableViewer.refresh();
				//
			} else if(input instanceof IScanMSD) {
				List<IMassSpectrumTarget> targetsToRemove = getMassSpectrumTargetList(table, indices);
				IScanMSD scan = (IScanMSD)input;
				scan.removeTargets(targetsToRemove);
				tableViewer.refresh();
			}
		}
	}

	private void manuallyVerifySelectedTargets() {

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
			List<IChromatogramTargetMSD> chromatogramTargets = getChromatogramTargetList(table, indices);
			for(IChromatogramTargetMSD chromatogramTarget : chromatogramTargets) {
				chromatogramTarget.setManuallyVerified(true);
			}
			tableViewer.refresh();
			//
		} else if(input instanceof IChromatogramPeakMSD) {
			List<IPeakTarget> peakTargets = getPeakTargetList(table, indices);
			for(IPeakTarget peakTarget : peakTargets) {
				peakTarget.setManuallyVerified(true);
			}
			tableViewer.refresh();
			//
		} else if(input instanceof IScanMSD) {
			List<IMassSpectrumTarget> massSpectrumTargets = getMassSpectrumTargetList(table, indices);
			for(IMassSpectrumTarget massSpectrumTarget : massSpectrumTargets) {
				massSpectrumTarget.setManuallyVerified(true);
			}
			tableViewer.refresh();
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
