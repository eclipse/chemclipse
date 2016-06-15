/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.internal.runnables.ImportChromatogramRunnable;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.swt.PeakTableViewerUI;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.support.ui.wizards.AbstractExtendedWizardPage;
import org.eclipse.chemclipse.swt.ui.components.chromatogram.SelectedPeakChromatogramUI;
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

public class PagePeakSelectionCSD extends AbstractExtendedWizardPage {

	private static final Logger logger = Logger.getLogger(PagePeakSelectionCSD.class);
	private IRetentionIndexWizardElements wizardElements;
	private SelectedPeakChromatogramUI selectedPeakChromatogramUI;
	private PeakTableViewerUI peakTableViewerUI;
	//
	private static final int PEAK_SHOW = 1;
	private static final int PEAKS_DELETE = 2;

	public PagePeakSelectionCSD(IRetentionIndexWizardElements wizardElements) {
		//
		super(PagePeakSelectionCSD.class.getName());
		setTitle("Peak Selection CSD");
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

	@Override
	public void setVisible(boolean visible) {

		super.setVisible(visible);
		if(visible) {
			IChromatogramSelectionCSD chromatogramSelectionCSD = getChromatogramSelectionCSD();
			wizardElements.setChromatogramSelectionCSD(chromatogramSelectionCSD);
			if(chromatogramSelectionCSD != null) {
				IChromatogramCSD chromatogramCSD = chromatogramSelectionCSD.getChromatogramCSD();
				List<IChromatogramPeakCSD> peaks = chromatogramCSD.getPeaks();
				peakTableViewerUI.setInput(peaks);
				if(peaks.size() > 0) {
					IChromatogramPeakCSD selectedPeak = peaks.get(0);
					chromatogramSelectionCSD.setSelectedPeak(selectedPeak);
					selectedPeakChromatogramUI.updateSelection(chromatogramSelectionCSD, true);
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
		createPeakTableField(composite);
		//
		validateSelection();
		setControl(composite);
	}

	private void createChromatogramField(Composite composite) {

		Composite parent = new Composite(composite, SWT.NONE);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new FillLayout());
		selectedPeakChromatogramUI = new SelectedPeakChromatogramUI(parent, SWT.BORDER);
	}

	private void createPeakTableField(Composite composite) {

		peakTableViewerUI = new PeakTableViewerUI(composite, SWT.BORDER | SWT.MULTI);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = 100;
		peakTableViewerUI.getTable().setLayoutData(gridData);
		peakTableViewerUI.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				propagateChange(PEAK_SHOW);
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
					messageBox.setText("Delete peak(s)");
					messageBox.setMessage("Would you like to delete the selected peak(s)?");
					if(messageBox.open() == SWT.YES) {
						propagateChange(PEAKS_DELETE);
					}
				}
			}
		});
	}

	private void propagateChange(int option) {

		Table table = peakTableViewerUI.getTable();
		int index = table.getSelectionIndex();
		Object object = peakTableViewerUI.getElementAt(index);
		IChromatogramSelectionCSD chromatogramSelectionCSD = wizardElements.getChromatogramSelectionCSD();
		if(chromatogramSelectionCSD != null && object instanceof IChromatogramPeakCSD) {
			/*
			 * Get the selected peak.
			 */
			IChromatogramPeakCSD selectedPeak = (IChromatogramPeakCSD)object;
			switch(option) {
				case PEAK_SHOW:
					chromatogramSelectionCSD.setSelectedPeak(selectedPeak);
					break;
				case PEAKS_DELETE:
					List<IChromatogramPeakCSD> peaksToDelete = getSelectionChromatogramPeakList();
					IChromatogramCSD chromatogramCSD = chromatogramSelectionCSD.getChromatogramCSD();
					chromatogramCSD.removePeaks(peaksToDelete);
					chromatogramSelectionCSD.reset();
					peakTableViewerUI.setInput(chromatogramCSD.getPeaks());
					break;
			}
			selectedPeakChromatogramUI.updateSelection(chromatogramSelectionCSD, true);
		}
	}

	private IChromatogramSelectionCSD getChromatogramSelectionCSD() {

		IChromatogramSelectionCSD chromatogramSelectionCSD = null;
		ImportChromatogramRunnable runnable = new ImportChromatogramRunnable(wizardElements);
		//
		try {
			getContainer().run(true, false, runnable);
			IChromatogram chromatogram = runnable.getChromatogram();
			if(chromatogram instanceof IChromatogramCSD) {
				IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
				chromatogramSelectionCSD = new ChromatogramSelectionCSD(chromatogramCSD);
			}
		} catch(InterruptedException e) {
			logger.warn(e);
		} catch(InvocationTargetException e) {
			logger.warn(e);
		} catch(ChromatogramIsNullException e) {
			logger.warn(e);
		}
		//
		return chromatogramSelectionCSD;
	}

	private List<IChromatogramPeakCSD> getSelectionChromatogramPeakList() {

		Table table = peakTableViewerUI.getTable();
		List<IChromatogramPeakCSD> peakList = new ArrayList<IChromatogramPeakCSD>();
		int[] indices = table.getSelectionIndices();
		for(int index : indices) {
			/*
			 * Get the selected item.
			 */
			TableItem tableItem = table.getItem(index);
			Object object = tableItem.getData();
			if(object instanceof IChromatogramPeakCSD) {
				IChromatogramPeakCSD chromatogramPeak = (IChromatogramPeakCSD)object;
				peakList.add(chromatogramPeak);
			}
		}
		return peakList;
	}

	private void validateSelection() {

		String message = null;
		if(wizardElements.getChromatogramWizardElementsCSD().getSelectedChromatograms().size() == 0) {
			message = "No chromatogram has been selected.";
		}
		//
		if(message == null) {
			if(wizardElements.getChromatogramSelectionCSD() == null) {
				message = "The chromatogram couldn't be loaded.";
			}
		}
		//
		if(message == null) {
			IChromatogramCSD chromatogramCSD = wizardElements.getChromatogramSelectionCSD().getChromatogramCSD();
			if(chromatogramCSD == null || chromatogramCSD.getPeaks().size() == 0) {
				message = "There is no peak available.";
			}
		}
		/*
		 * Updates the status
		 */
		updateStatus(message);
	}
}
