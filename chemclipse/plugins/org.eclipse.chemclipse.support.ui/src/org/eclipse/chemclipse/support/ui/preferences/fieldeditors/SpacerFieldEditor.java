/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.fieldeditors;

import org.eclipse.swt.widgets.Composite;

/**
 * You can use this editor as a spacer in a preference page.
 * 
 * @author Dr. Philip Wenig
 */
public class SpacerFieldEditor extends LabelFieldEditor {

	public SpacerFieldEditor(Composite parent) {
		super("", parent);
	}
}
