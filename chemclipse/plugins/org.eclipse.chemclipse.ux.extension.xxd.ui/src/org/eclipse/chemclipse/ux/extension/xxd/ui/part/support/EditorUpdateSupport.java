/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.part.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.ux.extension.ui.editors.IChromatogramEditor;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class EditorUpdateSupport {

	private static final Logger logger = Logger.getLogger(EditorUpdateSupport.class);
	private EPartService partService = ModelSupportAddon.getPartService();

	public List<IChromatogramSelection> getChromatogramSelections() {

		/*
		 * Get all open chromatogram parts.
		 */
		List<IChromatogramSelection> chromatogramSelections = new ArrayList<IChromatogramSelection>();
		if(partService != null) {
			/*
			 * TODO: Resolve why and when this happens! E.g. Chromatogram Overlay, after closing the preferences dialog.
			 * Exception "Application does not have an active window"
			 * is thrown here sometimes.
			 */
			try {
				Collection<MPart> parts = partService.getParts();
				for(MPart part : parts) {
					Object object = part.getObject();
					if(object != null) {
						/*
						 * MSD/CSD/WSD
						 */
						IChromatogramSelection selection = null;
						if(object instanceof IChromatogramEditor) {
							IChromatogramEditor editor = (IChromatogramEditor)object;
							selection = editor.getChromatogramSelection();
						}
						//
						if(selection != null) {
							chromatogramSelections.add(selection);
						}
					}
				}
			} catch(Exception e) {
				logger.warn(e);
			}
		}
		/*
		 * If the window was null and there was no open editor, the list will
		 * contains 0 elements.
		 */
		return chromatogramSelections;
	}
}
