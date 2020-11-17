/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - adjust to new constructor
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedNMROverlayUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class NMROverlayPart extends AbstractPart<ExtendedNMROverlayUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_SCAN_NMR_UPDATE_SELECTION;

	@Inject
	public NMROverlayPart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected ExtendedNMROverlayUI createControl(Composite parent) {

		return new ExtendedNMROverlayUI(parent, SWT.NONE);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			getControl().setFocus();
			return true;
		}
		//
		return false;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return TOPIC.equals(topic);
	}
}
