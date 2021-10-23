/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedPeakTracesUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class PeakTracesPart extends AbstractPart<ExtendedPeakTracesUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION;

	@Inject
	public PeakTracesPart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected ExtendedPeakTracesUI createControl(Composite parent) {

		return new ExtendedPeakTracesUI(parent, SWT.NONE);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			Object object = objects.get(0);
			if(isUpdateEvent(topic)) {
				if(object instanceof IPeak) {
					getControl().update((IPeak)object);
					return true;
				}
			} else if(isCloseEvent(topic)) {
				getControl().update(null);
				return false;
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
