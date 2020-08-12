/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.util.function.Predicate;

import org.eclipse.chemclipse.model.targets.ITargetDisplaySettings;
import org.eclipse.chemclipse.model.targets.ITargetReference;
import org.eclipse.chemclipse.model.targets.TargetReference;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class TargetReferenceLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String VISIBLE = "Visible";
	public static final String ID = "ID";
	public static final String TYPE = "Type";
	public static final String LIBRARY_FIELD = "Library Field";
	//
	private ITargetDisplaySettings targetDisplaySettings = null;
	private Predicate<ITargetReference> visibilityFilter = null;
	//
	public static final String[] TITLES = { //
			VISIBLE, //
			ID, //
			TYPE, //
			LIBRARY_FIELD //
	};
	//
	public static final int[] BOUNDS = { //
			30, //
			80, //
			50, //
			400 //
	};

	public TargetReferenceLabelProvider() {

		setTargetDisplaySettings(null);
	}

	public void setTargetDisplaySettings(ITargetDisplaySettings targetDisplaySettings) {

		this.targetDisplaySettings = targetDisplaySettings;
		visibilityFilter = TargetReference.createVisibilityFilter(targetDisplaySettings);
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			if(element instanceof ITargetReference) {
				ITargetReference targetReference = (ITargetReference)element;
				if(visibilityFilter.test(targetReference)) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED, IApplicationImage.SIZE_16x16);
				} else {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DESELECTED, IApplicationImage.SIZE_16x16);
				}
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		String text = "";
		if(element instanceof ITargetReference) {
			ITargetReference targetReference = (ITargetReference)element;
			switch(columnIndex) {
				case 0:
					text = ""; // Visibility
					break;
				case 1:
					text = targetReference.getID();
					break;
				case 2:
					text = targetReference.getType().getLabel();
					break;
				case 3:
					if(targetDisplaySettings != null) {
						text = targetReference.getTargetLabel(targetDisplaySettings.getLibraryField());
					}
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TARGETS, IApplicationImage.SIZE_16x16);
	}
}
