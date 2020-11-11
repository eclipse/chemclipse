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

import javax.inject.Inject;

import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedNMROverlayUI;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Composite;

public class NMROverlayPart extends AbstractPart {

	private ExtendedNMROverlayUI extendedNMROverlayUI;

	@Inject
	public NMROverlayPart(Composite parent, EPartService partservice) {

		extendedNMROverlayUI = new ExtendedNMROverlayUI(parent, partservice, Activator.getDefault().getPreferenceStore());
	}

	@Focus
	public void setFocus() {

		extendedNMROverlayUI.update();
	}
}
