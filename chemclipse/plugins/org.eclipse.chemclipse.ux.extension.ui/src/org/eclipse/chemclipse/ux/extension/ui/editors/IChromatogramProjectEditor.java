/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.editors;

import java.util.List;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

public interface IChromatogramProjectEditor extends IChemClipseEditor {

	@SuppressWarnings("rawtypes")
	List<IChromatogramSelection> getChromatogramSelections();
}
