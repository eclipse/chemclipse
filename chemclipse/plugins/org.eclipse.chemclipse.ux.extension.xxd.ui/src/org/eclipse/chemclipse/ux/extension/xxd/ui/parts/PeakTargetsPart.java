/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.AbstractPeakUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.IPeakUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedTargetsUI;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

public class PeakTargetsPart extends AbstractPeakUpdateSupport implements IPeakUpdateSupport {

	private ExtendedTargetsUI extendedTargetsUI;

	@Inject
	public PeakTargetsPart(Composite parent, MPart part) {
		super(part);
		extendedTargetsUI = new ExtendedTargetsUI(parent, part);
	}

	@Focus
	public void setFocus() {

		updatePeak(getPeak());
	}

	@Override
	public void updatePeak(IPeak peak) {

		extendedTargetsUI.update(peak);
	}
}
