/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import javax.inject.Inject;

import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedSequenceExplorerUI;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class SequenceExplorerPart {

	private ExtendedSequenceExplorerUI extendedSequenceExplorerUI;

	@Inject
	public SequenceExplorerPart(Composite parent, MPart part) {

		extendedSequenceExplorerUI = new ExtendedSequenceExplorerUI(parent, SWT.NONE);
	}

	@Focus
	public void setFocus() {

		extendedSequenceExplorerUI.setFocus();
	}
}
