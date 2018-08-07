/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.ux.extension.csd.ui.editors.ChromatogramEditorCSD;
import org.eclipse.chemclipse.ux.extension.msd.ui.editors.ChromatogramEditorMSD;
import org.eclipse.chemclipse.ux.extension.ui.views.AbstractChromatogramSelectionView;
import org.eclipse.chemclipse.ux.extension.wsd.ui.editors.ChromatogramEditorWSD;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public abstract class AbstractChromatogramOverlayView extends AbstractChromatogramSelectionView {

	private EPartService partService;

	public AbstractChromatogramOverlayView(MPart part, EPartService partService, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
		this.partService = partService;
	}

	/**
	 * Returns a list of chromatogram selections.
	 * If ignore overlay selection is true, the setting chromatogramSelection.isOverlaySelected() will
	 * be not evaluated.
	 * 
	 * @return List<IChromatogramSelection>
	 */
	@SuppressWarnings("rawtypes")
	public List<IChromatogramSelection> getChromatogramSelections(IChromatogramSelection masterChromatogramSelection, boolean ignoreOverlaySelection) {

		/*
		 * Add the master selection.
		 */
		List<IChromatogramSelection> chromatogramSelections = new ArrayList<IChromatogramSelection>();
		if(ignoreOverlaySelection) {
			chromatogramSelections.add(masterChromatogramSelection);
		} else {
			if(masterChromatogramSelection.isOverlaySelected()) {
				chromatogramSelections.add(masterChromatogramSelection);
			}
		}
		/*
		 * Get all open parts.
		 */
		Collection<MPart> parts = partService.getParts();
		for(MPart part : parts) {
			if(isChromatogramEditor(part)) {
				/*
				 * Select the chromatogram editor parts only.
				 */
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
					/*
					 * Do not add the master chromatogram selection twice.
					 */
					if(selection != null && selection != masterChromatogramSelection) {
						if(ignoreOverlaySelection) {
							chromatogramSelections.add(selection);
						} else {
							if(selection.isOverlaySelected()) {
								chromatogramSelections.add(selection);
							}
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
