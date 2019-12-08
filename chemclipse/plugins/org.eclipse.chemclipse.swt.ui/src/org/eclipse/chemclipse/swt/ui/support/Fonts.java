/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.support;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;

public class Fonts {

	private static Map<String, Font> fonts = new HashMap<String, Font>();

	/**
	 * Don't dispose the font as a cached version is used.
	 * In case of an error, the system font is returned.
	 * 
	 * @param display
	 * @param name
	 * @param height
	 * @param style
	 * @return
	 */
	public static Font getCachedFont(Device display, String name, int height, int style) {

		String fontId = name + height + style;
		if(!fonts.containsKey(fontId)) {
			Font font = new Font(display, name, height, style);
			if(font != null) {
				fonts.put(fontId, font);
			}
		}
		//
		return fonts.containsKey(fontId) ? fonts.get(fontId) : DisplayUtils.getDisplay().getSystemFont();
	}
}
