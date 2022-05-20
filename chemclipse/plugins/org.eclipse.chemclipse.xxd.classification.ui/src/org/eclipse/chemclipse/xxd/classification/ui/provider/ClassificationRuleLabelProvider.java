/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.classification.ui.provider;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.xxd.classification.model.ClassificationRule;
import org.eclipse.swt.graphics.Image;

public class ClassificationRuleLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String SEARCH_EXPRESSION = "Search Expression";
	public static final String CLASSIFICATION = "Classification";
	//
	public static final String[] TITLES = { //
			SEARCH_EXPRESSION, //
			CLASSIFICATION //
	};
	public static final int[] BOUNDS = { //
			200, //
			200, //
	};

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return getImage(element);
		} else {
			return null;
		}
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		String text = "";
		if(element instanceof ClassificationRule) {
			ClassificationRule rule = (ClassificationRule)element;
			switch(columnIndex) {
				case 0:
					text = rule.getSearchExpression();
					break;
				case 1:
					text = rule.getClassification();
					break;
				default:
					text = "n.a.";
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_LABELS, IApplicationImageProvider.SIZE_16x16);
	}
}