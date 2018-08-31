/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.EditorUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedChromatogramOverlayUI;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.widgets.Composite;

public class ChromatogramOverlayPart {

	private ExtendedChromatogramOverlayUI extendedChromatogramOverlayUI;
	private EditorUpdateSupport editorUpdateSupport = new EditorUpdateSupport();

	@Inject
	public ChromatogramOverlayPart(Composite parent) {
		extendedChromatogramOverlayUI = new ExtendedChromatogramOverlayUI(parent);
	}

	@Focus
	public void setFocus() {

		extendedChromatogramOverlayUI.update(editorUpdateSupport.getChromatogramSelections());
	}
}
