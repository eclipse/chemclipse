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

import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.AbstractDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.IDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedScanTableUI;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

public class ScanTablePart extends AbstractDataUpdateSupport implements IDataUpdateSupport {

	private ExtendedScanTableUI extendedScanTableUI;

	@Inject
	public ScanTablePart(Composite parent, MPart part) {
		super(part);
		extendedScanTableUI = new ExtendedScanTableUI(parent);
	}

	@Focus
	public void setFocus() {

		updateObject(getObject());
	}

	@Override
	public void updateObject(Object object) {

		extendedScanTableUI.update(object);
	}
}
