/*******************************************************************************
 * Copyright (c) 2014, 2020 Lablicate GmbH.
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

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.history.IEditInformation;
import org.eclipse.chemclipse.support.messages.ISupportMessages;
import org.eclipse.chemclipse.support.messages.SupportMessages;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class EditHistoryLabelProvider extends LabelProvider implements ITableLabelProvider {

	public static final String DATE = SupportMessages.INSTANCE().getMessage(ISupportMessages.COLUMN_DATE);
	public static final String DESCRIPTION = SupportMessages.INSTANCE().getMessage(ISupportMessages.COLUMN_DESCRIPTION);
	public static final String EDITOR = SupportMessages.INSTANCE().getMessage(ISupportMessages.COLUMN_EDITOR);
	//
	public static final String[] TITLES = {//
			DATE, //
			DESCRIPTION, //
			EDITOR //
	};
	//
	public static final int[] BOUNDS = {//
			100, //
			300, //
			100 //
	};

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		// Don't use an image.
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		String text = "";
		if(element instanceof IEditInformation) {
			IEditInformation editInformation = (IEditInformation)element;
			switch(columnIndex) {
				case 0:
					text = editInformation.getDate().toString();
					break;
				case 1:
					text = editInformation.getDescription();
					break;
				case 2:
					text = editInformation.getEditor();
					break;
				default:
					text = SupportMessages.INSTANCE().getMessage(ISupportMessages.LABEL_NOT_AVAILABLE);
			}
		}
		return text;
	}

	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_FILE, IApplicationImage.SIZE_16x16);
	}
}
