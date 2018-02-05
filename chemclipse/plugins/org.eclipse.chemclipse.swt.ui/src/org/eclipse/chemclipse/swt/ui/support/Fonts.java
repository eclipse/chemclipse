/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public class Fonts {

	private static Map<String, Font> fonts = new HashMap<String, Font>();

	public static Font getFont(String name, int height, int style) {

		Display display = Display.getDefault();
		String fontId = name + height + style;
		if(!fonts.containsKey(fontId)) {
			Font font = new Font(display, name, height, style);
			fonts.put(fontId, font);
		}
		//
		return fonts.get(fontId);
	}
}
