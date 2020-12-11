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
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedPeakDetectorUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class PeakDetectorPart extends AbstractPart<ExtendedPeakDetectorUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION;

	@Inject
	public PeakDetectorPart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected ExtendedPeakDetectorUI createControl(Composite parent) {

		return new ExtendedPeakDetectorUI(parent, SWT.NONE);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			if(isCloseEvent(topic)) {
				getControl().update(null);
				return false;
			} else {
				Object object = objects.get(0);
				if(object instanceof IChromatogramSelection) {
					IChromatogramSelection<?, ?> chromatogramSelection = (IChromatogramSelection<?, ?>)object;
					getControl().update(chromatogramSelection);
					return true;
				}
			}
		}
		//
		return false;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return isUpdateEvent(topic) || isCloseEvent(topic);
	}

	private boolean isUpdateEvent(String topic) {

		return TOPIC.equals(topic);
	}

	private boolean isCloseEvent(String topic) {

		return IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_CLOSE.equals(topic);
	}
}
