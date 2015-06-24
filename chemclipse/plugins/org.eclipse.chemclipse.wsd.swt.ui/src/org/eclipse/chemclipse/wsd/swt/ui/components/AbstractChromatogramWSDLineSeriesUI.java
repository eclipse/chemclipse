/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.swt.ui.components;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.swt.ui.components.AbstractChromatogramLineSeriesUI;
import org.eclipse.chemclipse.swt.ui.support.AxisTitlesIntensityScale;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.notifier.IChromatogramSelectionWSDUpdateNotifier;

public abstract class AbstractChromatogramWSDLineSeriesUI extends AbstractChromatogramLineSeriesUI implements IChromatogramSelectionWSDUpdateNotifier {

	public AbstractChromatogramWSDLineSeriesUI(Composite parent, int style) {

		super(parent, style, new AxisTitlesIntensityScale());
	}

	@Override
	public void update(IChromatogramSelectionWSD chromatogramSelection, boolean forceReload) {

		updateSelection(chromatogramSelection, forceReload);
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {

		super.mouseDoubleClick(e);
		/*
		 * Perform an update only if the current composite is in master mode.
		 */
		if(isMaster()) {
			/*
			 * Get the scan number
			 */
			Rectangle rect = getPlotArea().getClientArea();
			double lower = getAxisSet().getXAxis(0).getRange().lower;
			double upper = getAxisSet().getXAxis(0).getRange().upper;
			double delta = upper - lower + 1;
			double part = delta / rect.width * e.x;
			int retentionTime = (int)(lower + part);
			int scan = getChromatogramSelection().getChromatogram().getScanNumber(retentionTime);
			/*
			 * Chromatogram Selection
			 * It's a WSD selection, cause the update method stores a WSD chromatogram.
			 * Anyhow, it needs to be checked.
			 */
			IChromatogramSelection storedChromatogramSelection = getChromatogramSelection();
			if(storedChromatogramSelection instanceof IChromatogramSelectionWSD) {
				IChromatogramSelectionWSD chromatogramSelection = (IChromatogramSelectionWSD)storedChromatogramSelection;
				IScanWSD selectedScan = chromatogramSelection.getChromatogramWSD().getSupplierScan(scan);
				/*
				 * Set selected scan will fire an update.
				 */
				chromatogramSelection.setSelectedScan(selectedScan);
			}
		}
	}
}
