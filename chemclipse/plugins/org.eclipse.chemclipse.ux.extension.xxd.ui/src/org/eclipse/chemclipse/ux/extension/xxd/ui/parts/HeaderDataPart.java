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

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.parts.AbstractOverviewUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.parts.IOverviewUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedHeaderDataUI;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

public class HeaderDataPart extends AbstractOverviewUpdateSupport implements IOverviewUpdateSupport {

	private ExtendedHeaderDataUI extendedHeaderDataUI;

	@Inject
	public HeaderDataPart(Composite parent, MPart part) {
		super(part);
		extendedHeaderDataUI = new ExtendedHeaderDataUI(parent);
	}

	@Focus
	public void setFocus() {

		updateObjects(getObjects(), getTopic());
	}

	@Override
	public void updateChromatogramOverview(IChromatogramOverview chromatogramOverview) {

		if(chromatogramOverview != null) {
			extendedHeaderDataUI.update(chromatogramOverview, false);
		} else {
			extendedHeaderDataUI.update(null, false);
		}
	}
}
