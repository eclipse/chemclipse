/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.preferences;

import org.eclipse.jface.preference.FieldEditor;

import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.AbstractTabFolderPreferencePage;

public abstract class AbstractCustomTabFolderPreferencePage extends AbstractTabFolderPreferencePage implements ICustomPreferencePage {

	@Override
	public void addFieldEditor(FieldEditor editor) {

		addField(editor);
	}
}
