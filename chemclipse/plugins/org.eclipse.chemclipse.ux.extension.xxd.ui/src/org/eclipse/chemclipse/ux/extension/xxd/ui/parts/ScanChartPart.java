/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - improve user feedback for unsaved changes
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedScanChartUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ScanChartPart extends AbstractPart<ExtendedScanChartUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION;
	private IChromatogram cachedChromatogram = null;

	@Inject
	public ScanChartPart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected ExtendedScanChartUI createControl(Composite parent) {

		return new ExtendedScanChartUI(parent, SWT.NONE);
	}

	private boolean hasChanged(IChromatogramSelection chromatogramSelection) {

		if(chromatogramSelection != null) {
			if(chromatogramSelection.getChromatogram() != cachedChromatogram) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			if(isCloseEvent(topic)) {
				getControl().update(null);
				return true;
			} else {
				Object object = objects.get(0);
				IScan scan = null;
				if(object instanceof IScan) {
					scan = (IScan)object;
				} else if(object instanceof IPeak) {
					IPeak peak = (IPeak)object;
					scan = peak.getPeakModel().getPeakMaximum();
				} else if(isChromatogramTopic(topic)) {
					IChromatogramSelection<?, ?> chromatogramSelection = (IChromatogramSelection<?, ?>)object;
					if(hasChanged(chromatogramSelection)) {
						scan = chromatogramSelection.getSelectedScan();
					}
				}
				getControl().update(scan);
				return true;
			}
		}
		//
		return false;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return isChromatogramTopic(topic) || isScanTopic(topic) || isPeakTopic(topic) || isCloseEvent(topic);
	}

	private boolean isChromatogramTopic(String topic) {

		return IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION.equals(topic);
	}

	private boolean isScanTopic(String topic) {

		return TOPIC.equals(topic);
	}

	private boolean isPeakTopic(String topic) {

		return IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION.equals(topic);
	}

	private boolean isCloseEvent(String topic) {

		return IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_CLOSE.equals(topic);
	}
}
