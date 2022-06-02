/*******************************************************************************
 * Copyright (c) 2016, 2022 Lablicate GmbH.
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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.internal.runnables.ChromatogramImportRunnable;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.support.ui.wizards.AbstractExtendedWizardPage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.custom.ChromatogramPeakChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedScanChartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.PeakTableRetentionIndexViewerUI;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class PagePeakSelection extends AbstractExtendedWizardPage {

	private static final Logger logger = Logger.getLogger(PagePeakSelection.class);
	//
	private RetentionIndexWizardElements wizardElements;
	private ChromatogramPeakChart chromatogramPeakChart;
	private ExtendedScanChartUI extendedScanChartUI;
	private PeakTableRetentionIndexViewerUI peakTableViewerUI;
	//
	private static final int PEAK_SHOW = 1;
	private static final int PEAKS_DELETE = 2;

	public PagePeakSelection(RetentionIndexWizardElements wizardElements) {

		super(PagePeakSelection.class.getName());
		setTitle("Peak Selection");
		setDescription("Please select the peaks that shall be used.");
		this.wizardElements = wizardElements;
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

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void setVisible(boolean visible) {

		super.setVisible(visible);
		if(visible) {
			IChromatogramSelection chromatogramSelection = getChromatogramSelection();
			wizardElements.setChromatogramSelection(chromatogramSelection);
			if(chromatogramSelection != null) {
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				/*
				 * Hide the mass spectrum view if it is CSD data.
				 */
				boolean isVisible = (chromatogram instanceof IChromatogramMSD) ? true : false;
				GridData gridData = (GridData)extendedScanChartUI.getLayoutData();
				gridData.exclude = !isVisible;
				extendedScanChartUI.setVisible(isVisible);
				Composite parent = extendedScanChartUI.getParent();
				parent.layout(false);
				parent.redraw();
				//
				List<? extends IPeak> peaks = chromatogram.getPeaks();
				peakTableViewerUI.setInput(peaks);
				if(peaks.size() > 0) {
					List<IPeak> selectedPeaks = new ArrayList<>();
					IPeak selectedPeak = peaks.get(0);
					selectedPeaks.add(selectedPeak);
					chromatogramSelection.setSelectedPeak(selectedPeak);
					updateChromatogramChart(chromatogramSelection);
					updateSelectedPeaksInChart(selectedPeaks);
					if(selectedPeak instanceof IPeakMSD) {
						IPeakMSD peakMSD = (IPeakMSD)selectedPeak;
						extendedScanChartUI.update(peakMSD.getExtractedMassSpectrum());
					}
				}
			}
			validateSelection();
		}
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		//
		createChromatogramField(composite);
		createScanChart(composite);
		createPeakTableField(composite);
		//
		validateSelection();
		setControl(composite);
	}

	private void createChromatogramField(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new FillLayout());
		chromatogramPeakChart = new ChromatogramPeakChart(composite, SWT.BORDER);
	}

	private void createScanChart(Composite parent) {

		extendedScanChartUI = new ExtendedScanChartUI(parent, SWT.NONE);
		extendedScanChartUI.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void createPeakTableField(Composite parent) {

		peakTableViewerUI = new PeakTableRetentionIndexViewerUI(parent, SWT.BORDER | SWT.MULTI);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = 100;
		peakTableViewerUI.getTable().setLayoutData(gridData);
		peakTableViewerUI.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				propagateChange(PEAK_SHOW);
				updateSelectedPeaksInChart(getSelectionChromatogramPeakList());
			}
		});
		//
		peakTableViewerUI.getControl().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == 127 && e.stateMask == 0) {
					/*
					 * Press "DEL" button.
					 */
					MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.YES | SWT.NO | SWT.ICON_WARNING);
					messageBox.setText("Delete peaks");
					messageBox.setMessage("Would you like to delete the selected peaks?");
					if(messageBox.open() == SWT.YES) {
						propagateChange(PEAKS_DELETE);
					}
				}
			}
		});
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void propagateChange(int option) {

		Table table = peakTableViewerUI.getTable();
		int index = table.getSelectionIndex();
		Object object = peakTableViewerUI.getElementAt(index);
		IChromatogramSelection chromatogramSelection = wizardElements.getChromatogramSelection();
		if(chromatogramSelection != null && object instanceof IPeak) {
			/*
			 * Get the selected peak.
			 */
			IPeak selectedPeak = (IPeak)object;
			switch(option) {
				case PEAK_SHOW:
					chromatogramSelection.setSelectedPeak(selectedPeak);
					if(selectedPeak instanceof IPeakMSD) {
						IPeakMSD peakMSD = (IPeakMSD)selectedPeak;
						extendedScanChartUI.update(peakMSD.getExtractedMassSpectrum());
					}
					break;
				case PEAKS_DELETE:
					List<? extends IPeak> peaksToDelete = getSelectionChromatogramPeakList();
					IChromatogram chromatogram = chromatogramSelection.getChromatogram();
					chromatogram.removePeaks(peaksToDelete);
					chromatogramSelection.reset();
					peakTableViewerUI.setInput(chromatogram.getPeaks());
					updateChromatogramChart(chromatogramSelection);
					updateSelectedPeaksInChart(null);
					break;
			}
		}
	}

	@SuppressWarnings({"rawtypes"})
	private IChromatogramSelection getChromatogramSelection() {

		IChromatogramSelection chromatogramSelection = null;
		ChromatogramImportRunnable runnable = new ChromatogramImportRunnable(wizardElements);
		//
		try {
			getContainer().run(true, false, runnable);
			IChromatogram chromatogram = runnable.getChromatogram();
			if(chromatogram instanceof IChromatogramMSD) {
				IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
				chromatogramSelection = new ChromatogramSelectionMSD(chromatogramMSD);
			} else if(chromatogram instanceof IChromatogramCSD) {
				IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
				chromatogramSelection = new ChromatogramSelectionCSD(chromatogramCSD);
			}
		} catch(InterruptedException e) {
			logger.warn(e);
		} catch(InvocationTargetException e) {
			logger.warn(e);
			logger.warn(e.getCause());
		} catch(ChromatogramIsNullException e) {
			logger.warn(e);
		}
		//
		return chromatogramSelection;
	}

	private List<IPeak> getSelectionChromatogramPeakList() {

		Table table = peakTableViewerUI.getTable();
		List<IPeak> peakList = new ArrayList<>();
		int[] indices = table.getSelectionIndices();
		for(int index : indices) {
			/*
			 * Get the selected item.
			 */
			TableItem tableItem = table.getItem(index);
			Object object = tableItem.getData();
			if(object instanceof IPeak) {
				IPeak chromatogramPeak = (IPeak)object;
				peakList.add(chromatogramPeak);
			}
		}
		return peakList;
	}

	@SuppressWarnings({"rawtypes"})
	private void validateSelection() {

		String message = null;
		if(wizardElements.getSelectedChromatograms().size() == 0) {
			message = "No chromatogram has been selected.";
		}
		//
		if(message == null) {
			if(wizardElements.getChromatogramSelection() == null) {
				message = "The chromatogram couldn't be loaded.";
			}
		}
		//
		if(message == null) {
			IChromatogram chromatogram = wizardElements.getChromatogramSelection().getChromatogram();
			if(chromatogram == null || chromatogram.getPeaks().size() == 0) {
				message = "There is no peak available.";
			}
		}
		/*
		 * Updates the status
		 */
		updateStatus(message);
	}

	@SuppressWarnings("rawtypes")
	private void updateChromatogramChart(IChromatogramSelection chromatogramSelection) {

		chromatogramPeakChart.updateChromatogram(chromatogramSelection);
	}

	private void updateSelectedPeaksInChart(List<IPeak> selectedPeaks) {

		chromatogramPeakChart.updatePeaks(selectedPeaks);
	}
}
