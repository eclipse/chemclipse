/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - allow zoom-lock
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.EditorUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedChromatogramOverlayUI;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ChromatogramOverlayPart extends AbstractPart<ExtendedChromatogramOverlayUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION;
	private final EditorUpdateSupport editorUpdateSupport = new EditorUpdateSupport();

	@Inject
	public ChromatogramOverlayPart(Composite parent) {

		super(parent, TOPIC);
	}

	public static class LockZoomHandler {

		@Execute
		public void execute(MPart part, MDirectToolItem toolItem) {

			Object object = part.getObject();
			if(object instanceof ChromatogramOverlayPart) {
				ChromatogramOverlayPart overlayPart = (ChromatogramOverlayPart)object;
				overlayPart.getControl().setZoomLocked(toolItem.isSelected());
			}
		}
	}

	@Override
	protected ExtendedChromatogramOverlayUI createControl(Composite parent) {

		return new ExtendedChromatogramOverlayUI(parent, SWT.BORDER);
	}

	@Focus
	public void setFocus() {

		getControl().update(editorUpdateSupport.getChromatogramSelections());
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		// No action required. Action on Focus.
		return true;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		// No action required. Action on Focus.
		return false;
	}
}
