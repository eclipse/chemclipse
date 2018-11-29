/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.model.identifier.ITargetTemplate;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class TargetTemplateLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String NAME = "Name";
	public static final String CAS = "CAS";
	public static final String COMMENT = "Comment";
	public static final String CONTRIBUTOR = "Contributor";
	public static final String REFERENCE_ID = "Reference ID";
	//
	public static final String[] TITLES = { //
			NAME, //
			CAS, //
			COMMENT, //
			CONTRIBUTOR, //
			REFERENCE_ID//
	};
	public static final int[] BOUNDS = { //
			200, //
			100, //
			100, //
			100, //
			100 //
	};

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return getImage(element);
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		String text = "";
		if(element instanceof ITargetTemplate) {
			ITargetTemplate targetTemplate = (ITargetTemplate)element;
			switch(columnIndex) {
				case 0:
					text = targetTemplate.getName();
					break;
				case 1:
					text = targetTemplate.getCasNumber();
					break;
				case 2:
					text = targetTemplate.getComment();
					break;
				case 3:
					text = targetTemplate.getContributor();
					break;
				case 4:
					text = targetTemplate.getReferenceId();
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TARGETS, IApplicationImage.SIZE_16x16);
	}
}
