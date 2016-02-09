/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.peak;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.PeakCheckBoxEditingSupport;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.PeakListContentProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.PeakListLabelProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.PeakListTableComparator;
import org.eclipse.chemclipse.msd.swt.ui.support.MassSpectrumFileSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class PeakListUI {

	private static final Logger logger = Logger.getLogger(PeakListUI.class);
	//
	private ExtendedTableViewer tableViewer;
	private PeakListTableComparator peakListTableComparator;
	private static final String PEAK_IS_ACTIVE_FOR_ANALYSIS = "Active for Analysis";
	private String[] titles = {PEAK_IS_ACTIVE_FOR_ANALYSIS, "RT (minutes)", "RI", "Area", "Start RT", "Stop RT", "Width", "Scan# at Peak Maximum", "S/N", "Leading", "Tailing", "Model Description", "Suggested Components"};
	private int bounds[] = {30, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};

	public PeakListUI(Composite parent, int style) {
		initialize(parent);
	}

	public void setFocus() {

		tableViewer.getControl().setFocus();
	}

	public void update(IPeaks peaks, boolean forceReload) {

		if(peaks != null) {
			tableViewer.setInput(peaks);
		}
	}

	public void clear() {

		tableViewer.setInput(null);
	}

	public ExtendedTableViewer getTableViewer() {

		return tableViewer;
	}

	public String[] getTitles() {

		return titles;
	}

	/**
	 * Deletes the selected peaks
	 * 
	 */
	public void deleteSelectedPeaks(IChromatogramSelectionMSD chromatogramSelection) {

		/*
		 * Delete the selected items.
		 */
		Table table = tableViewer.getTable();
		int[] indices = table.getSelectionIndices();
		List<IChromatogramPeakMSD> peaksToDelete = getChromatogramPeakList(table, indices);
		/*
		 * Delete peaks in table.
		 */
		table.remove(indices);
		/*
		 * Delete peak in chromatogram.
		 */
		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
		chromatogram.removePeaks(peaksToDelete);
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
	}

	private void createButtons(Composite composite) {

		Composite compositeButtons = new Composite(composite, SWT.NONE);
		GridData gridDataComposite = new GridData(GridData.FILL_HORIZONTAL);
		gridDataComposite.horizontalAlignment = SWT.END;
		compositeButtons.setLayoutData(gridDataComposite);
		compositeButtons.setLayout(new GridLayout(3, false));
		//
		Button uncheckAllButton = new Button(compositeButtons, SWT.PUSH);
		uncheckAllButton.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_UNCHECK_ALL, IApplicationImage.SIZE_16x16));
		uncheckAllButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setActiveForAnalysis(false);
			}
		});
		//
		Button checkAllButton = new Button(compositeButtons, SWT.PUSH);
		checkAllButton.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHECK_ALL, IApplicationImage.SIZE_16x16));
		checkAllButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setActiveForAnalysis(true);
			}
		});
		//
		Button saveButton = new Button(compositeButtons, SWT.PUSH);
		saveButton.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAVE_AS, IApplicationImage.SIZE_16x16));
		saveButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					List<IChromatogramPeakMSD> peaks = getChromatogramPeakList();
					IMassSpectra massSpectra = new MassSpectra();
					for(IChromatogramPeakMSD peak : peaks) {
						massSpectra.addMassSpectrum(peak.getExtractedMassSpectrum());
					}
					MassSpectrumFileSupport.saveMassSpectra(massSpectra);
				} catch(NoConverterAvailableException e1) {
					logger.warn(e1);
				}
			}
		});
	}

	private void setActiveForAnalysis(boolean activeForAnalysis) {

		List<IChromatogramPeakMSD> peaks = getChromatogramPeakList();
		for(IChromatogramPeakMSD peak : peaks) {
			peak.setActiveForAnalysis(activeForAnalysis);
		}
		tableViewer.refresh();
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
		/*
		 * Copy and Paste of the table content.
		 */
		tableViewer.getTable().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				/*
				 * The selected content will be placed to the clipboard if the
				 * user is using "Function + c". "Function-Key" 262144
				 * (stateMask) + "c" 99 (keyCode)
				 */
				if(e.keyCode == 99 && e.stateMask == 262144) {
					tableViewer.copyToClipboard(titles);
				}
			}
		});
	}

	private List<IChromatogramPeakMSD> getChromatogramPeakList() {

		List<IChromatogramPeakMSD> peakList = new ArrayList<IChromatogramPeakMSD>();
		Table table = tableViewer.getTable();
		for(TableItem tableItem : table.getItems()) {
			Object object = tableItem.getData();
			if(object instanceof IChromatogramPeakMSD) {
				IChromatogramPeakMSD chromatogramPeak = (IChromatogramPeakMSD)object;
				peakList.add(chromatogramPeak);
			}
		}
		return peakList;
	}

	private List<IChromatogramPeakMSD> getChromatogramPeakList(Table table, int[] indices) {

		List<IChromatogramPeakMSD> peakList = new ArrayList<IChromatogramPeakMSD>();
		for(int index : indices) {
			/*
			 * Get the selected item.
			 */
			TableItem tableItem = table.getItem(index);
			Object object = tableItem.getData();
			if(object instanceof IChromatogramPeakMSD) {
				IChromatogramPeakMSD chromatogramPeak = (IChromatogramPeakMSD)object;
				peakList.add(chromatogramPeak);
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
}