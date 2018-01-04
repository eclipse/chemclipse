/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.support;

import java.util.List;

import org.eclipse.swt.graphics.Color;

public class ColorScheme implements IColorScheme {

	private int index = 0;
	private List<Color> colors;

	public ColorScheme(List<Color> colors) {
		this.colors = colors;
	}

	@Override
	public Color getColor(int i) {

		if(i >= 0 && i < colors.size()) {
			return colors.get(i);
		} else {
			if(colors.size() == 0) {
				return null;
			} else {
				return colors.get(0);
			}
		}
	}

	@Override
	public Color getNextColor() {

		index++;
		validatePosition();
		return colors.get(index);
	}

	@Override
	public Color getPreviousColor() {

		index--;
		validatePosition();
		return colors.get(index);
	}

	@Override
	public int size() {

		return colors.size();
	}

	/**
	 * Validates the position.<br/>
	 * If the position is lower than list size, it will start at sizes last
	 * element.<br/< If it is greater than the last element, it will begin at
	 * the first element.
	 */
	private void validatePosition() {

		if(index < 0) {
			index = size() - 1;
		} else if(index >= size()) {
			index = 0;
		}
	}

	@Override
	public void reset() {

		index = 0;
	}

	@Override
	public Color getColor() {

		validatePosition();
		return colors.get(index);
	}

	@Override
	public void incrementColor() {

		index++;
		validatePosition();
	}
}
