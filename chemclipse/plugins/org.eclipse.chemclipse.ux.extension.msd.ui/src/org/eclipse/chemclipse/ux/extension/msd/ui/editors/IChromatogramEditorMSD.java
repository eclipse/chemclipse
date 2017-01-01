/*******************************************************************************
 * Copyright (c) 2010, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.editors;

import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.ux.extension.ui.editors.IChemClipseEditor;

/**
 * @author Dr. Philip Wenig
 * 
 */
public interface IChromatogramEditorMSD extends IChemClipseEditor {

	/**
	 * Returns an instance of the chromatogram selection.
	 * 
	 * @return {@link IChromatogramSelectionMSD}
	 */
	IChromatogramSelectionMSD getChromatogramSelection();
}
