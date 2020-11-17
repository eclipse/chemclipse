/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedScanBrowseUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ScanBrowsePart extends AbstractPart<ExtendedScanBrowseUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION;

	@Inject
	public ScanBrowsePart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected ExtendedScanBrowseUI createControl(Composite parent) {

		return new ExtendedScanBrowseUI(parent, SWT.NONE);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			Object object = objects.get(0);
			if(isChromatogramTopic(topic)) {
				if(object instanceof IChromatogramSelection) {
					getControl().update((IChromatogramSelection<?, ?>)object);
					return true;
				}
			}
		}
		//
		return false;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return isChromatogramTopic(topic);
	}

	private boolean isChromatogramTopic(String topic) {

		if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION)) {
			return true;
		} else if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			return true;
		} else if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			return true;
		} else if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			return true;
		} else {
			return false;
		}
	}
}
