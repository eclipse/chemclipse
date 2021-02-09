/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.peak;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.comparator.TargetExtendedComparator;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.PeakCheckBoxEditingSupport;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.PeakListContentProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.PeakListLabelProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.PeakListTableComparator;
import org.eclipse.chemclipse.msd.swt.ui.support.DatabaseFileSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class PeakListUI {

	private static final Logger logger = Logger.getLogger(PeakListUI.class);
	//
	@SuppressWarnings("rawtypes")
	private IChromatogramSelection chromatogramSelection;
	//
	private final DecimalFormat decimalFormat;
	//
	private ExtendedTableViewer tableViewer;
	private Label labelSelectedPeak;
	private Label labelPeaks;
	//
	private PeakListTableComparator peakListTableComparator;
	private static final String PEAK_IS_ACTIVE_FOR_ANALYSIS = "Active for Analysis";
	private final String[] titles = {PEAK_IS_ACTIVE_FOR_ANALYSIS, "RT (min)", "RI", "Area", "Start RT", "Stop RT", "Width", "Scan# at Peak Maximum", "S/N", "Leading", "Tailing", "Model Description", "Suggested Components", "Name"};
	private final int bounds[] = {30, 100, 60, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
	//
	private final TargetExtendedComparator targetExtendedComparator;

	public PeakListUI(Composite parent, int style) {

		decimalFormat = ValueFormat.getDecimalFormatEnglish();
		targetExtendedComparator = new TargetExtendedComparator(SortOrder.DESC);
		initialize(parent);
	}

	@SuppressWarnings("rawtypes")
	public void setChromatogramSelection(IChromatogramSelection chromatogramSelection) {

		this.chromatogramSelection = chromatogramSelection;
	}

	public void setFocus() {

		tableViewer.getControl().setFocus();
	}

	public void update(IPeaks<?> peaks, boolean forceReload) {

		if(peaks != null) {
			if(chromatogramSelection != null && chromatogramSelection.getChromatogram() != null) {
				labelPeaks.setText(chromatogramSelection.getChromatogram().getNumberOfPeaks() + " chromatogram peaks - " + peaks.getPeaks().size() + " displayed peaks");
			} else {
				labelPeaks.setText(peaks.getPeaks().size() + " displayed peaks");
			}
			//
			tableViewer.setInput(peaks);
		} else {
			clear();
		}
	}

	public void setLabelSelectedPeak(IChromatogramPeakMSD selectedPeakMSD) {

		if(selectedPeakMSD != null && selectedPeakMSD.getPeakModel() != null) {
			IPeakModelMSD peakModel = selectedPeakMSD.getPeakModel();
			String name = getName(selectedPeakMSD.getTargets());
			labelSelectedPeak.setText("Selected Peak: " + decimalFormat.format(peakModel.getRetentionTimeAtPeakMaximum() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR) + " min - Name: " + name);
		} else {
			labelSelectedPeak.setText("Selected Peak: none selected yet");
		}
	}

	public void clear() {

		labelSelectedPeak.setText("");
		labelPeaks.setText("");
		tableViewer.setInput(null);
	}

	public ExtendedTableViewer getTableViewer() {

		return tableViewer;
	}

	public String[] getTitles() {

		return titles;
	}

	public void deleteSelectedPeaks(IChromatogramSelectionMSD chromatogramSelection) {

		if(chromatogramSelection != null) {
			/*
			 * Delete the selected items.
			 */
			Table table = tableViewer.getTable();
			int[] indices = table.getSelectionIndices();
			List<IPeakMSD> peaksToDelete = getPeakList(table, indices);
			List<IChromatogramPeakMSD> chromatogramPeaksToDelete = new ArrayList<IChromatogramPeakMSD>();
			for(IPeakMSD peakMSD : peaksToDelete) {
				if(peakMSD instanceof IChromatogramPeakMSD) {
					chromatogramPeaksToDelete.add((IChromatogramPeakMSD)peakMSD);
				}
			}
			/*
			 * Delete peaks in table.
			 */
			table.remove(indices);
			/*
			 * Delete peak in chromatogram.
			 */
			IChromatogramMSD chromatogram = chromatogramSelection.getChromatogram();
			chromatogram.removePeaks(chromatogramPeaksToDelete);
			/*
			 * Is the chromatogram updatable? IChromatogramSelection
			 * at itself isn't.
			 */
			if(chromatogramSelection instanceof ChromatogramSelectionMSD) {
				ChromatogramSelectionMSD chromSelection = (ChromatogramSelectionMSD)chromatogramSelection;
				List<IChromatogramPeakMSD> peaks = chromatogram.getPeaks();
				if(peaks.size() > 0) {
					chromSelection.setSelectedPeak(peaks.get(0));
				}
				chromSelection.update(true); // true: forces the editor to update
			}
		}
	}

	public void setActiveStatusSelectedPeaks(IChromatogramSelectionMSD chromatogramSelection, boolean activeForAnalysis) {

		Table table = tableViewer.getTable();
		int[] indices = table.getSelectionIndices();
		List<IPeakMSD> peaks = getPeakList(table, indices);
		for(IPeakMSD peak : peaks) {
			peak.setActiveForAnalysis(activeForAnalysis);
		}
		tableViewer.refresh();
		chromatogramSelection.update(true);
	}

	public void exportSelectedPeaks(IChromatogramSelectionMSD chromatogramSelection) {

		try {
			Table table = tableViewer.getTable();
			int[] indices = table.getSelectionIndices();
			List<IPeakMSD> peaks = getPeakList(table, indices);
			DatabaseFileSupport.saveMassSpectra(peaks);
		} catch(NoConverterAvailableException e1) {
			logger.warn(e1);
		}
	}

	private void initialize(Composite parent) {

		parent.setLayout(new FillLayout());
		//
		Composite composite = new Composite(parent, SWT.FILL);
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 1;
		composite.setLayout(layout);
		//
		createButtons(composite);
		createTable(composite);
		createInfos(composite);
	}

	private void createButtons(Composite composite) {

		Composite compositeButtons = new Composite(composite, SWT.NONE);
		GridData gridDataComposite = new GridData(GridData.FILL_HORIZONTAL);
		gridDataComposite.horizontalAlignment = SWT.END;
		compositeButtons.setLayoutData(gridDataComposite);
		compositeButtons.setLayout(new GridLayout(3, false));
		//
		createUncheckAllButton(compositeButtons);
		createCheckAllButton(compositeButtons);
		createSaveButton(compositeButtons);
	}

	private void createUncheckAllButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Uncheck the selected peaks.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_UNCHECK_ALL, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setActiveForAnalysis(false);
			}
		});
	}

	private void createCheckAllButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Check the selected peaks.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHECK_ALL, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setActiveForAnalysis(true);
			}
		});
	}

	private void createSaveButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Save the peaks");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAVE_AS, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					List<IPeakMSD> peaks = getPeakList();
					DatabaseFileSupport.saveMassSpectra(peaks);
				} catch(NoConverterAvailableException e1) {
					logger.warn(e1);
				}
			}
		});
	}

	private void createTable(Composite composite) {

		// SWT.VIRTUAL | SWT.FULL_SELECTION
		tableViewer = new ExtendedTableViewer(composite, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		tableViewer.createColumns(titles, bounds);
		tableViewer.setContentProvider(new PeakListContentProvider());
		tableViewer.setLabelProvider(new PeakListLabelProvider());
		/*
		 * Sorting the table.
		 */
		peakListTableComparator = new PeakListTableComparator();
		tableViewer.setComparator(peakListTableComparator);
		setEditingSupport();
	}

	private void createInfos(Composite composite) {

		labelSelectedPeak = new Label(composite, SWT.NONE);
		labelSelectedPeak.setText("");
		labelSelectedPeak.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		labelPeaks = new Label(composite, SWT.NONE);
		labelPeaks.setText("");
		labelPeaks.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void setActiveForAnalysis(boolean activeForAnalysis) {

		List<IPeakMSD> peaks = getPeakList();
		for(IPeakMSD peak : peaks) {
			peak.setActiveForAnalysis(activeForAnalysis);
		}
		tableViewer.refresh();
		chromatogramSelection.update(true);
	}

	private List<IPeakMSD> getPeakList() {

		List<IPeakMSD> peakList = new ArrayList<IPeakMSD>();
		Table table = tableViewer.getTable();
		for(TableItem tableItem : table.getItems()) {
			Object object = tableItem.getData();
			if(object instanceof IPeakMSD) {
				IPeakMSD peak = (IPeakMSD)object;
				peakList.add(peak);
			}
		}
		return peakList;
	}

	private List<IPeakMSD> getPeakList(Table table, int[] indices) {

		List<IPeakMSD> peakList = new ArrayList<IPeakMSD>();
		for(int index : indices) {
			/*
			 * Get the selected item.
			 */
			TableItem tableItem = table.getItem(index);
			Object object = tableItem.getData();
			if(object instanceof IPeakMSD) {
				IPeakMSD peak = (IPeakMSD)object;
				peakList.add(peak);
			}
		}
		return peakList;
	}

	private void setEditingSupport() {

		TableViewer tableViewer = getTableViewer();
		List<TableViewerColumn> tableViewerColumns = this.tableViewer.getTableViewerColumns();
		for(int i = 0; i < tableViewerColumns.size(); i++) {
			TableViewerColumn tableViewerColumn = tableViewerColumns.get(i);
			String label = tableViewerColumn.getColumn().getText();
			if(label.equals(PEAK_IS_ACTIVE_FOR_ANALYSIS)) {
				tableViewerColumn.setEditingSupport(new PeakCheckBoxEditingSupport(tableViewer));
			}
		}
	}

	private String getName(Set<IIdentificationTarget> targets) {

		ILibraryInformation libraryInformation = IIdentificationTarget.getBestLibraryInformation(targets, targetExtendedComparator);
		if(libraryInformation != null) {
			return libraryInformation.getName();
		} else {
			return "Peak is not identified yet.";
		}
	}
}