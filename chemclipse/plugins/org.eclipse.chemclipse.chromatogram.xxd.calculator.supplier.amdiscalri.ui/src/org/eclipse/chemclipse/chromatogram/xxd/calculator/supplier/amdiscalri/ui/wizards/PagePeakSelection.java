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
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.internal.runnables.ImportChromatogramRunnable;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.swt.ChromatogramPeakTableViewerUI;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.swt.ui.components.chromatogram.SelectedPeakChromatogramUI;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassValueDisplayPrecision;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.SimpleMassSpectrumUI;
import org.eclipse.chemclipse.support.ui.wizards.AbstractExtendedWizardPage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

public class PagePeakSelection extends AbstractExtendedWizardPage {

	private static final Logger logger = Logger.getLogger(PagePeakSelection.class);
	private IRetentionIndexWizardElements wizardElements;
	private SelectedPeakChromatogramUI selectedPeakChromatogramUI;
	private SimpleMassSpectrumUI simpleMassSpectrumUI;
	private ChromatogramPeakTableViewerUI chromatogramPeakTableViewerUI;
	private IChromatogramSelectionMSD chromatogramSelectionMSD;

	public PagePeakSelection(IRetentionIndexWizardElements wizardElements) {
		//
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

	@Override
	public void setVisible(boolean visible) {

		super.setVisible(visible);
		if(visible) {
			IChromatogramMSD chromatogramMSD = getChromatogramMSD();
			wizardElements.setChromatogramMSD(chromatogramMSD);
			try {
				chromatogramSelectionMSD = new ChromatogramSelectionMSD(chromatogramMSD);
				List<IChromatogramPeakMSD> peaks = chromatogramMSD.getPeaks();
				chromatogramPeakTableViewerUI.setInput(peaks);
				if(peaks.size() > 0) {
					IChromatogramPeakMSD selectedPeak = peaks.get(0);
					chromatogramSelectionMSD.setSelectedPeak(selectedPeak);
					selectedPeakChromatogramUI.update(chromatogramSelectionMSD, true);
					simpleMassSpectrumUI.update(selectedPeak.getExtractedMassSpectrum(), true);
				}
			} catch(ChromatogramIsNullException e) {
				logger.warn(e);
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

		chromatogramPeakTableViewerUI = new ChromatogramPeakTableViewerUI(composite, SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = 100;
		chromatogramPeakTableViewerUI.getTable().setLayoutData(gridData);
		chromatogramPeakTableViewerUI.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				Table table = chromatogramPeakTableViewerUI.getTable();
				int index = table.getSelectionIndex();
				Object object = chromatogramPeakTableViewerUI.getElementAt(index);
				if(chromatogramSelectionMSD != null && object instanceof IChromatogramPeakMSD) {
					IChromatogramPeakMSD selectedPeak = (IChromatogramPeakMSD)object;
					/*
					 * Update chromatogram and mass spectrum
					 */
					chromatogramSelectionMSD.setSelectedPeak(selectedPeak);
					selectedPeakChromatogramUI.update(chromatogramSelectionMSD, true);
					simpleMassSpectrumUI.update(selectedPeak.getExtractedMassSpectrum(), true);
				}
			}
		});
	}

	private IChromatogramMSD getChromatogramMSD() {

		IChromatogramMSD chromatogramMSD = null;
		ImportChromatogramRunnable runnable = new ImportChromatogramRunnable(wizardElements);
		//
		try {
			getContainer().run(true, false, runnable);
			chromatogramMSD = runnable.getChromatogramMSD();
		} catch(InterruptedException e) {
			logger.warn(e);
		} catch(InvocationTargetException e) {
			logger.warn(e);
		}
		//
		return chromatogramMSD;
	}

	private void validateSelection() {

		String message = null;
		if(wizardElements.getSelectedChromatograms().size() == 0) {
			message = "No chromatogram has been selected.";
		}
		//
		if(message == null) {
			if(wizardElements.getChromatogramMSD() == null) {
				message = "The chromatogram couldn't be loaded.";
			}
		}
		//
		if(message == null) {
			if(wizardElements.getChromatogramMSD().getPeaks().size() == 0) {
				message = "No peak is available.";
			}
		}
		/*
		 * Updates the status
		 */
		updateStatus(message);
	}
}
