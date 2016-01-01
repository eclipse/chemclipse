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
package org.eclipse.chemclipse.ux.extension.wsd.ui.editors;

import org.eclipse.chemclipse.ux.extension.ui.editors.IChemClipseEditor;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;

public interface IChromatogramEditorWSD extends IChemClipseEditor {

	/**
	 * Returns an instance of the chromatogram selection.
	 * 
	 * @return {@link IChromatogramSelectionWSD}
	 */
	IChromatogramSelectionWSD getChromatogramSelection();
}
