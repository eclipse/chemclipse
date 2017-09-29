/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
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

import javax.inject.Inject;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MArea;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

@SuppressWarnings("unused")
public class TaskQuickAccessPart {

	@Inject
	private MApplication application;
	@Inject
	private EModelService modelService;
	@Inject
	private EPartService partService;
	//
	// private String AREA = "org.eclipse.chemclipse.rcp.app.ui.editor";
	//
	private String PARTDESCRIPTOR_CHROMATOGRAM_OVERLAY = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.chromatogramOverlayPartDescriptor";
	//
	private String PARTSTACK_FILES = "org.eclipse.chemclipse.ux.extension.xxd.ui.partstack.files";
	private String PARTSTACK_OVERVIEW = "org.eclipse.chemclipse.ux.extension.xxd.ui.partstack.overview";
	private String PARTSTACK_BOTTOM_LEFT = "org.eclipse.chemclipse.ux.extension.xxd.ui.partstack.bottom.left";
	private String PARTSTACK_BOTTOM_CENTER = "org.eclipse.chemclipse.ux.extension.xxd.ui.partstack.bottom.center";
	private String PARTSTACK_BOTTOM_RIGHT = "org.eclipse.chemclipse.ux.extension.xxd.ui.partstack.bottom.right";

	@Inject
	public TaskQuickAccessPart(Composite parent) {
		initialize(parent);
	}

	private void initialize(Composite parent) {

		/*
		 * Add buttons here to focus specialized views.
		 */
		parent.setLayout(new RowLayout());
		createButtonOverlayTask(parent);
	}

	private void createButtonOverlayTask(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Overlay Modus");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM_OVERLAY, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				MPart part = getPart(PARTDESCRIPTOR_CHROMATOGRAM_OVERLAY, PARTSTACK_BOTTOM_LEFT);
				togglePartVisibility(part);
				boolean isPartVisible = isPartVisible(part);
				// setPartStackVisibility(PARTSTACK_FILES, !isPartVisible);
				// setPartStackVisibility(PARTSTACK_OVERVIEW, !isPartVisible);
				// setPartStackVisibility(PARTSTACK_BOTTOM_CENTER, !isPartVisible);
				// setPartStackVisibility(PARTSTACK_BOTTOM_RIGHT, !isPartVisible);
				// setAreaVisibility(AREA, !isPartVisible);
			}
		});
	}

	private MPart getPart(String partId, String partStackId) {

		MPart part = null;
		MUIElement element = modelService.find(partId, application);
		if(element instanceof MPart) {
			/*
			 * Get the part or create it.
			 */
			part = (MPart)element;
			if(!partService.getParts().contains(part)) {
				partService.createPart(part.getElementId());
			}
		} else {
			part = partService.createPart(partId);
			MPartStack partStack = (MPartStack)modelService.find(partStackId, application);
			partStack.getChildren().add(part);
		}
		//
		return part;
	}

	private void togglePartVisibility(MPart part) {

		if(part != null) {
			if(isPartVisible(part)) {
				partService.hidePart(part);
			} else {
				partService.showPart(part, PartState.ACTIVATE);
			}
		}
	}

	private boolean isPartVisible(MPart part) {

		if(part != null) {
			if(partService.isPartVisible(part)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	private void setPartStackVisibility(String partStackId, boolean visible) {

		MPartStack partStack = (MPartStack)modelService.find(partStackId, application);
		if(partStack != null) {
			partStack.setVisible(visible);
		}
	}

	private void setAreaVisibility(String areaId, boolean visible) {

		MArea area = (MArea)modelService.find(areaId, application);
		if(area != null) {
			area.setVisible(visible);
		}
	}
}
