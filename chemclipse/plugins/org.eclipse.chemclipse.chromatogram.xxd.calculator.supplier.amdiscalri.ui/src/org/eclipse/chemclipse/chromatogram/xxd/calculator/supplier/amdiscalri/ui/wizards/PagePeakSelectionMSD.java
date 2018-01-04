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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.internal.runnables.ChromatogramImportRunnable;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassValueDisplayPrecision;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.SimpleMassSpectrumUI;
import org.eclipse.chemclipse.support.ui.wizards.AbstractExtendedWizardPage;
import org.eclipse.chemclipse.swt.ui.components.chromatogram.SelectedPeakChromatogramUI;
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

public class PagePeakSelectionMSD extends AbstractExtendedWizardPage {

	private static final Logger logger = Logger.getLogger(PagePeakSelectionMSD.class);
	private IRetentionIndexWizardElements wizardElements;
	private SelectedPeakChromatogramUI selectedPeakChromatogramUI;
	private SimpleMassSpectrumUI simpleMassSpectrumUI;
	private PeakTableRetentionIndexViewerUI peakTableViewerUI;
	//
	private static final int PEAK_SHOW = 1;
	private static final int PEAKS_DELETE = 2;

	public PagePeakSelectionMSD(IRetentionIndexWizardElements wizardElements) {
		//
		super(PagePeakSelectionMSD.class.getName());
		setTitle("Peak Selection MSD");
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
			IChromatogramSelectionMSD chromatogramSelectionMSD = getChromatogramSelectionMSD();
			wizardElements.setChromatogramSelectionMSD(chromatogramSelectionMSD);
			if(chromatogramSelectionMSD != null) {
				IChromatogramMSD chromatogramMSD = chromatogramSelectionMSD.getChromatogramMSD();
				List<IChromatogramPeakMSD> peaks = chromatogramMSD.getPeaks();
				peakTableViewerUI.setInput(peaks);
				if(peaks.size() > 0) {
					IChromatogramPeakMSD selectedPeak = peaks.get(0);
					chromatogramSelectionMSD.setSelectedPeak(selectedPeak);
					selectedPeakChromatogramUI.updateSelection(chromatogramSelectionMSD, true);
					simpleMassSpectrumUI.update(selectedPeak.getExtractedMassSpectrum(), true);
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
		createMassSpectrumField(composite);
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

	private void createMassSpectrumField(Composite composite) {

		Composite parent = new Composite(composite, SWT.NONE);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new FillLayout());
		simpleMassSpectrumUI = new SimpleMassSpectrumUI(parent, SWT.BORDER, MassValueDisplayPrecision.NOMINAL);
	}

	private void createPeakTableField(Composite composite) {

		peakTableViewerUI = new PeakTableRetentionIndexViewerUI(composite, SWT.BORDER | SWT.MULTI);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
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
		IChromatogramSelectionMSD chromatogramSelectionMSD = wizardElements.getChromatogramSelectionMSD();
		if(chromatogramSelectionMSD != null && object instanceof IChromatogramPeakMSD) {
			/*
			 * Get the selected peak.
			 */
			IChromatogramPeakMSD selectedPeak = (IChromatogramPeakMSD)object;
			switch(option) {
				case PEAK_SHOW:
					chromatogramSelectionMSD.setSelectedPeak(selectedPeak);
					simpleMassSpectrumUI.update(selectedPeak.getExtractedMassSpectrum(), true);
					break;
				case PEAKS_DELETE:
					List<IChromatogramPeakMSD> peaksToDelete = getSelectionChromatogramPeakList();
					IChromatogramMSD chromatogramMSD = chromatogramSelectionMSD.getChromatogramMSD();
					chromatogramMSD.removePeaks(peaksToDelete);
					chromatogramSelectionMSD.reset();
					peakTableViewerUI.setInput(chromatogramMSD.getPeaks());
					break;
			}
			selectedPeakChromatogramUI.updateSelection(chromatogramSelectionMSD, true);
		}
	}

	private IChromatogramSelectionMSD getChromatogramSelectionMSD() {

		IChromatogramSelectionMSD chromatogramSelectionMSD = null;
		ChromatogramImportRunnable runnable = new ChromatogramImportRunnable(wizardElements);
		//
		try {
			getContainer().run(true, false, runnable);
			IChromatogram chromatogram = runnable.getChromatogram();
			if(chromatogram instanceof IChromatogramMSD) {
				IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
				chromatogramSelectionMSD = new ChromatogramSelectionMSD(chromatogramMSD);
			}
		} catch(InterruptedException e) {
			logger.warn(e);
		} catch(InvocationTargetException e) {
			logger.warn(e);
		} catch(ChromatogramIsNullException e) {
			logger.warn(e);
		}
		//
		return chromatogramSelectionMSD;
	}

	private List<IChromatogramPeakMSD> getSelectionChromatogramPeakList() {

		Table table = peakTableViewerUI.getTable();
		List<IChromatogramPeakMSD> peakList = new ArrayList<IChromatogramPeakMSD>();
		int[] indices = table.getSelectionIndices();
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

	private void validateSelection() {

		String message = null;
		if(wizardElements.getChromatogramWizardElementsMSD().getSelectedChromatograms().size() == 0) {
			message = "No chromatogram has been selected.";
		}
		//
		if(message == null) {
			if(wizardElements.getChromatogramSelectionMSD() == null) {
				message = "The chromatogram couldn't be loaded.";
			}
		}
		//
		if(message == null) {
			IChromatogramMSD chromatogramMSD = wizardElements.getChromatogramSelectionMSD().getChromatogramMSD();
			if(chromatogramMSD == null || chromatogramMSD.getPeaks().size() == 0) {
				message = "There is no peak available.";
			}
		}
		/*
		 * Updates the status
		 */
		updateStatus(message);
	}
}
