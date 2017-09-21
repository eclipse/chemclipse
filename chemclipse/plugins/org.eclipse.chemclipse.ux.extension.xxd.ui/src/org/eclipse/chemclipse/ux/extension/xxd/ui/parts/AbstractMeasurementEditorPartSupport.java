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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.ux.extension.csd.ui.editors.ChromatogramEditorCSD;
import org.eclipse.chemclipse.ux.extension.msd.ui.editors.ChromatogramEditorMSD;
import org.eclipse.chemclipse.ux.extension.wsd.ui.editors.ChromatogramEditorWSD;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public abstract class AbstractMeasurementEditorPartSupport {

	public List<IChromatogramSelection> getChromatogramSelections(EPartService partService) {

		List<IChromatogramSelection> chromatogramSelections = new ArrayList<IChromatogramSelection>();
		if(partService != null) {
			/*
			 * Get all open chromatogram parts.
			 */
			Collection<MPart> parts = partService.getParts();
			for(MPart part : parts) {
				if(isChromatogramEditor(part)) {
					Object object = part.getObject();
					if(object != null) {
						/*
						 * MSD/CSD/WSD
						 */
						IChromatogramSelection selection = null;
						if(object instanceof ChromatogramEditorMSD) {
							ChromatogramEditorMSD editor = (ChromatogramEditorMSD)object;
							selection = editor.getChromatogramSelection();
						} else if(object instanceof ChromatogramEditorCSD) {
							ChromatogramEditorCSD editor = (ChromatogramEditorCSD)object;
							selection = editor.getChromatogramSelection();
						} else if(object instanceof ChromatogramEditorWSD) {
							ChromatogramEditorWSD editor = (ChromatogramEditorWSD)object;
							selection = editor.getChromatogramSelection();
						}
						//
						if(selection != null) {
							chromatogramSelections.add(selection);
						}
					}
				}
			}
		}
		/*
		 * If the window was null and there was no open editor, the list will
		 * contains 0 elements.
		 */
		return chromatogramSelections;
	}

	private boolean isChromatogramEditor(MPart part) {

		return (part.getElementId().equals(ChromatogramEditorMSD.ID) || //
				part.getElementId().equals(ChromatogramEditorCSD.ID) || //
				part.getElementId().equals(ChromatogramEditorWSD.ID));
	}
}
