/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedScanTableUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ScanTablePart extends AbstractPart<ExtendedScanTableUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION;

	@Inject
	public ScanTablePart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected ExtendedScanTableUI createControl(Composite parent) {

		return new ExtendedScanTableUI(parent, SWT.NONE);
	}

	@Override
	public void setFocus() {

		ExtendedScanTableUI control = getControl();
		if(control != null) {
			getControl().setFocus();
		}
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			if(isScanEvent(topic) || isPeakEvent(topic)) {
				getControl().setInput(objects.get(0));
				return true;
			} else if(isCloseEvent(topic)) {
				getControl().setInput(null);
				unloadData();
				return false;
			}
		}
		//
		return false;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return isScanEvent(topic) || isPeakEvent(topic) || isCloseEvent(topic);
	}

	private boolean isScanEvent(String topic) {

		return IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION.equals(topic);
	}

	private boolean isPeakEvent(String topic) {

		return IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION.equals(topic);
	}

	private boolean isCloseEvent(String topic) {

		return IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_CLOSE.equals(topic);
	}
}