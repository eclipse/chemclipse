/*******************************************************************************
 * Copyright (c) 2015, 2016 Dr. Philip Wenig, Matthias Mailänder.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.massspectrum;

import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.IonListContentProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.MassSpectrumPeakLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class MassSpectrumPeakListUI {

	private ExtendedTableViewer tableViewer;
	private String[] titles = {"m/z", "intensity"};
	private int bounds[] = {100, 100};

	public MassSpectrumPeakListUI(Composite parent, int style) {
		parent.setLayout(new FillLayout());
		/*
		 * E.g. Scan
		 */
		tableViewer = new ExtendedTableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.createColumns(titles, bounds);
		tableViewer.setContentProvider(new IonListContentProvider());
		tableViewer.setLabelProvider(new MassSpectrumPeakLabelProvider());
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

	public void update(IScanMSD massSpectrum, boolean forceReload) {

		if(massSpectrum != null) {
			tableViewer.setInput(massSpectrum);
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
