/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
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

public enum DisplayType {

	TIC("TIC", "Total Intensity Chromatogram"), // in MS - total ion chromatogram
	BPC("BPC", "Base Peak Chromatogram"), //
	XIC("XIC", "Extracted Ion Chromatogram"), // EIP - extracted ion profiles
	SIC("SIC", "Selected Ion Chromatogram"), //
	XWC("XWC", "Extracted Wavelength Chromatogram"), //
	SWC("SWC", "Selected Wavelength Chromatogram"), //
	MPC("MPC", "Max Plot Chromatogram"), //
	TSC("TSC", "Total Subtracted Chromatogram"), //
	SRM("SRM", "Single Reaction Monitoring"), //
	MRM("MRM", "Multiple Reaction Monitoring");

	private static Map<String, DisplayType> shortcutMap = new HashMap<>();
	static {
		for(DisplayType displayType : DisplayType.values()) {
			shortcutMap.put(displayType.shortcut, displayType);
		}
	}
	//
	private String shortcut;
	private String description;

	private DisplayType(String shortcut, String description) {

		this.shortcut = shortcut;
		this.description = description;
	}

	public String getShortcut() {

		return shortcut;
	}

	public String getDescription() {

		return description;
	}

	@Override
	public String toString() {

		return description;
	}

	public static String toDescription(Collection<DisplayType> types) {

		return toDescription(types.toArray(new DisplayType[types.size()]));
	}

	public static String toDescription(DisplayType... types) {

		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < types.length; i++) {
			sb.append(types[i].toString());
			if(i + 1 < types.length) {
				sb.append("+");
			}
		}
		return sb.toString();
	}

	public static String toShortcut(Collection<DisplayType> types) {

		return toShortcut(types.toArray(new DisplayType[types.size()]));
	}

	public static String toShortcut(DisplayType... types) {

		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < types.length; i++) {
			sb.append(types[i].getShortcut());
			if(i + 1 < types.length) {
				sb.append("+");
			}
		}
		return sb.toString();
	}

	public static Set<DisplayType> toDisplayTypes(String shortcut) {

		return Arrays.stream(shortcut.split("\\+")).map(s -> shortcutMap.get(s)).collect(Collectors.toSet());
	}
}
