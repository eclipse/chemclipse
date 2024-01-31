/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import jakarta.inject.Inject;

import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.MassSpectrumPseudoGelUI;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class MassSpectrumPseudoGelPart extends AbstractPart<MassSpectrumPseudoGelUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION;

	@Inject
	public MassSpectrumPseudoGelPart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected MassSpectrumPseudoGelUI createControl(Composite parent) {

		return new MassSpectrumPseudoGelUI(parent, SWT.BORDER);
	}

	@Override
	@Focus
	public void setFocus() {

		getControl().update();
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
