/*******************************************************************************
 * Copyright (c) 2017, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - improve user feedback for unsaved changes
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedScanChartUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import jakarta.inject.Inject;

public class ScanChartPart extends AbstractPart<ExtendedScanChartUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION;

	@Inject
	public ScanChartPart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected ExtendedScanChartUI createControl(Composite parent) {

		return new ExtendedScanChartUI(parent, SWT.NONE);
	}

	@Override
	public void setFocus() {

		ExtendedScanChartUI control = getControl();
		if(control != null) {
			getControl().setFocus();
		}
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			if(isCloseEvent(topic)) {
				getControl().update(null);
				return true;
			} else {
				Object object = objects.get(0);
				IScan updateScan = null;
				if(object instanceof IScan scan) {
					updateScan = scan;
				} else if(object instanceof IPeak peak) {
					updateScan = peak.getPeakModel().getPeakMaximum();
				}
				getControl().update(updateScan);
				return true;
			}
		}
		//
		return false;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return isScanTopic(topic) || isPeakTopic(topic) || isCloseEvent(topic);
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