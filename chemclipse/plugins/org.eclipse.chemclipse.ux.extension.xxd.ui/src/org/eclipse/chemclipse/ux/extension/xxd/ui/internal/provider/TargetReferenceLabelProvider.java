/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
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
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.swt.graphics.Image;

public class TargetReferenceLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String VISIBLE = ExtensionMessages.visible;
	public static final String ID = ExtensionMessages.id;
	public static final String TYPE = ExtensionMessages.type;
	public static final String LIBRARY_FIELD = ExtensionMessages.libraryField;
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
			if(element instanceof ITargetReference targetReference) {
				if(visibilityFilter.test(targetReference)) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED, IApplicationImageProvider.SIZE_16x16);
				} else {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DESELECTED, IApplicationImageProvider.SIZE_16x16);
				}
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		String text = "";
		if(element instanceof ITargetReference targetReference) {
			switch(columnIndex) {
				case 0:
					text = ""; // Visibility
					break;
				case 1:
					text = targetReference.getID();
					break;
				case 2:
					text = targetReference.getType().label();
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

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TARGETS, IApplicationImageProvider.SIZE_16x16);
	}
}
