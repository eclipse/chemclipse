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
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import javax.inject.Inject;

import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedXIROverlayUI;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.widgets.Composite;

public class XIROverlayPart extends AbstractPart {

	private ExtendedXIROverlayUI extendedXIROverlayUI;

	@Inject
	public XIROverlayPart(Composite parent) {

		extendedXIROverlayUI = new ExtendedXIROverlayUI(parent);
	}

	@Focus
	public void setFocus() {

		extendedXIROverlayUI.update();
	}
}
