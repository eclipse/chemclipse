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

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.AbstractSubtractUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ISubtractUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedSubtractScanUI;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

public class SubtractScanPart extends AbstractSubtractUpdateSupport implements ISubtractUpdateSupport {

	private ExtendedSubtractScanUI extendedSubtractChartUI;

	@Inject
	public SubtractScanPart(Composite parent, MPart part) {
		super(part);
		extendedSubtractChartUI = new ExtendedSubtractScanUI(parent, part);
	}

	@Focus
	public void setFocus() {

		updateScan(getScan());
	}

	@Override
	public void updateScan(IScan scan) {

		extendedSubtractChartUI.update(scan);
	}
}
