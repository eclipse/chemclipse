/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.csd.ui.editors;

import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.ux.extension.ui.editors.IChemClipseEditor;

public interface IChromatogramEditorCSD extends IChemClipseEditor {

	/**
	 * Returns an instance of the chromatogram selection.
	 * 
	 * @return {@link IChromatogramSelectionCSD}
	 */
	IChromatogramSelectionCSD getChromatogramSelection();
}
