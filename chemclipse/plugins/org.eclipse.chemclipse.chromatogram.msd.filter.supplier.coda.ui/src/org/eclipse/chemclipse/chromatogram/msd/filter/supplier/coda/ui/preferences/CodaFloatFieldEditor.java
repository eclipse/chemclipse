/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.ui.preferences;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.FloatFieldEditor;

public class CodaFloatFieldEditor extends FloatFieldEditor {

	public CodaFloatFieldEditor(String name, String labelText, float minValue, float maxValue, Composite parent) {
		super(name, labelText, minValue, maxValue, parent);
	}
}
