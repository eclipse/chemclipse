/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
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

import jakarta.inject.Inject;

import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedRegularExpressionUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class RegularExpressionPart {

	@Inject
	public RegularExpressionPart(Composite parent) {

		new ExtendedRegularExpressionUI(parent, SWT.NONE);
	}
}
