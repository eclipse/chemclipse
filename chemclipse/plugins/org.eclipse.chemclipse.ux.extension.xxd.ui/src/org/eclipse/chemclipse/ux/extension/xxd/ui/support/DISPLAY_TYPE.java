/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public enum DISPLAY_TYPE {
	TIC("TIC", "Total Intensity Chromatogram"), //
	BPC("BPC", "Base Peak Chromatogram"), //
	XIC("XIC", "Extracted Ion Chromatogram"), //
	SIC("SIC", "Selected Ion Chromatogram"), //
	XWC("XWC", "Extracted Wavelength Chromatogram"), //
	SWC("SWC", "Selected Wavelength Chromatogram"), //
	TSC("TSC", "Total Subtracted Chromatogram"), //
	SRM("SRM", "Single Reaction Monitoring"), //
	MRM("MRM", "Multiple Reaction Monitoring");

	private static Map<String, DISPLAY_TYPE> shortcutMap = new HashMap<>();
	static {
		for(DISPLAY_TYPE displayType : DISPLAY_TYPE.values()) {
			shortcutMap.put(displayType.shortucut, displayType);
		}
	}
	//
	private String shortucut;
	private String description;

	private DISPLAY_TYPE(String shortcut, String description) {

		this.shortucut = shortcut;
		this.description = description;
	}

	public String getShortcut() {

		return shortucut;
	}

	public String getDescription() {

		return description;
	}

	@Override
	public String toString() {

		return description;
	}

	public static String toDescription(Collection<DISPLAY_TYPE> types) {

		return toDescription(types.toArray(new DISPLAY_TYPE[types.size()]));
	}

	public static String toDescription(DISPLAY_TYPE... types) {

		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < types.length; i++) {
			sb.append(types[i].toString());
			if(i + 1 < types.length) {
				sb.append("+");
			}
		}
		return sb.toString();
	}

	public static String toShortcut(Collection<DISPLAY_TYPE> types) {

		return toShortcut(types.toArray(new DISPLAY_TYPE[types.size()]));
	}

	public static String toShortcut(DISPLAY_TYPE... types) {

		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < types.length; i++) {
			sb.append(types[i].getShortcut());
			if(i + 1 < types.length) {
				sb.append("+");
			}
		}
		return sb.toString();
	}

	public static Set<DISPLAY_TYPE> toDisplayTypes(String shortcut) {

		return Arrays.stream(shortcut.split("\\+")).map(s -> shortcutMap.get(s)).collect(Collectors.toSet());
	}
}
