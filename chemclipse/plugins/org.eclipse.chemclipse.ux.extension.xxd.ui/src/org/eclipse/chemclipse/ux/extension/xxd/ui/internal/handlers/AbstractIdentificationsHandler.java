/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.handlers;

import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractIdentificationsHandler {

	private static IChromatogramSelection<?, ?> chromatogramSelection;

	public void setChromatogramSelection(IChromatogramSelection<?, ?> chromatogramSelectionX) {

		chromatogramSelection = chromatogramSelectionX;
	}

	public void deleteIdentifications(Shell shell, String text, boolean deleteChromatogramIdentifications, boolean deletePeakIdentifications, boolean deleteScanIdentifications) {

		if(chromatogramSelection != null) {
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram != null) {
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
				messageBox.setText(text);
				messageBox.setMessage("Do you really want to delete the identifications?");
				int decision = messageBox.open();
				if(SWT.YES == decision) {
					/*
					 * Chromatogram
					 */
					if(deleteChromatogramIdentifications) {
						chromatogram.getTargets().clear();
					}
					/*
					 * Peaks
					 */
					if(deletePeakIdentifications) {
						List<? extends IPeak> peaks = ChromatogramDataSupport.getPeaks(chromatogram);
						for(IPeak peak : peaks) {
							peak.getTargets().clear();
						}
					}
					/*
					 * Scans
					 */
					if(deleteScanIdentifications) {
						List<IScan> scans = ChromatogramDataSupport.getIdentifiedScans(chromatogramSelection.getChromatogram());
						for(IScan scan : scans) {
							scan.getTargets().clear();
						}
					}
					/*
					 * Update
					 */
					update();
				}
			}
		}
	}

	private void update() {

		chromatogramSelection.update(true);
	}
}
