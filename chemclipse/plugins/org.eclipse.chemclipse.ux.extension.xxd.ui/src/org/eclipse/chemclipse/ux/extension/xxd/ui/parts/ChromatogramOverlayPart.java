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

import javax.inject.Inject;

import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.EditorUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedChromatogramOverlayUI;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Composite;

public class ChromatogramOverlayPart extends AbstractPart {

	private final ExtendedChromatogramOverlayUI extendedChromatogramOverlayUI;
	private final EditorUpdateSupport editorUpdateSupport = new EditorUpdateSupport();

	@Inject
	public ChromatogramOverlayPart(Composite parent, EModelService modelService, MApplication application, EPartService partService) {

		extendedChromatogramOverlayUI = new ExtendedChromatogramOverlayUI(parent);
		extendedChromatogramOverlayUI.update(modelService, application, partService);
	}

	@Focus
	public void setFocus() {

		extendedChromatogramOverlayUI.update(editorUpdateSupport.getChromatogramSelections());
	}

	public static class LockZoomHandler {

		@Execute
		public void execute(MPart part, MDirectToolItem toolItem) {

			Object object = part.getObject();
			if(object instanceof ChromatogramOverlayPart) {
				ChromatogramOverlayPart overlayPart = (ChromatogramOverlayPart)object;
				overlayPart.extendedChromatogramOverlayUI.setZoomLocked(toolItem.isSelected());
			}
		}
	}
}
