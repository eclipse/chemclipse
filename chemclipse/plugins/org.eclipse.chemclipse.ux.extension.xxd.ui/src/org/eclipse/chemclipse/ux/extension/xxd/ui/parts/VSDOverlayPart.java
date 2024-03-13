/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedVSDOverlayUI;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import jakarta.inject.Inject;

public class VSDOverlayPart extends AbstractPart<ExtendedVSDOverlayUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_SCAN_VSD_UPDATE_SELECTION;

	@Inject
	public VSDOverlayPart(Composite parent) {

		super(parent, TOPIC);
	}

	@Focus
	public void setFocus() {

		getControl().update();
	}

	@Override
	protected ExtendedVSDOverlayUI createControl(Composite parent) {

		return new ExtendedVSDOverlayUI(parent, SWT.NONE);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		// No action required. Action on Focus.
		return true;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		// No action required. Action on Focus.
		return false;
	}
}
