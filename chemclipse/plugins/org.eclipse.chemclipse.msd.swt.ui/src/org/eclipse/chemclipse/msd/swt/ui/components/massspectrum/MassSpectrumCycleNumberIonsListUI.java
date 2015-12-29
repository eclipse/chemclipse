/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.massspectrum;

import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.IonListContentProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.IonListLabelProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.IonListTableComparator;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class MassSpectrumCycleNumberIonsListUI {

	private ExtendedTableViewer tableViewer;
	private IonListTableComparator ionListTableComparator;
	private String[] titles = {"m/z", "abundance", "parent m/z", "parent resolution", "daughter m/z", "daughter resolution", "collision energy"};
	private int bounds[] = {100, 100, 120, 120, 120, 120, 120};

	public MassSpectrumCycleNumberIonsListUI(Composite parent, int style) {
		parent.setLayout(new FillLayout());
		/*
		 * E.g. Scan
		 */
		tableViewer = new ExtendedTableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.createColumns(titles, bounds);
		tableViewer.setContentProvider(new IonListContentProvider());
		tableViewer.setLabelProvider(new IonListLabelProvider());
		/*
		 * Sorting the table.
		 */
		ionListTableComparator = new IonListTableComparator();
		tableViewer.setComparator(ionListTableComparator);
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

	public void setFocus() {

		tableViewer.getControl().setFocus();
	}

	public void update(IChromatogramSelectionMSD chromatogramSelection, boolean forceReload) {

		if(chromatogramSelection != null) {
			IScanMSD massSpectrum = chromatogramSelection.getSelectedScan();
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram != null && massSpectrum != null) {
				int cycleNumber = massSpectrum.getCycleNumber();
				if(cycleNumber > 1) {
					List<IScan> scans = chromatogram.getScanCycleScans(cycleNumber);
					IScanMSD massSpectrumCycleNumber = new ScanMSD();
					for(IScan scan : scans) {
						if(scan instanceof IScanMSD) {
							IScanMSD scanMSD = (IScanMSD)scan;
							List<IIon> ions = scanMSD.getIons();
							for(IIon ion : ions) {
								massSpectrumCycleNumber.addIon(ion, false);
							}
						}
					}
					tableViewer.setInput(massSpectrumCycleNumber);
				} else {
					tableViewer.setInput(massSpectrum);
				}
			}
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
}
