/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts;

import org.eclipse.swt.graphics.FontData;

public class TargetLabel {

	// constant values
	private final String label;
	private final String id;
	private final boolean isActive;
	private final double x;
	private final double y;
	private final FontData fontData;
	/*
	 * Cached values used for calculation
	 */
	private LabelBounds bounds;

	public TargetLabel(String label, String id, FontData fontData, boolean isActive, double x, double y) {

		this.label = label;
		this.id = id;
		this.fontData = fontData;
		this.isActive = isActive;
		this.x = x;
		this.y = y;
	}

	public String getLabel() {

		return label;
	}

	public String getId() {

		return id;
	}

	public boolean isActive() {

		return isActive;
	}

	public double getX() {

		return x;
	}

	public double getY() {

		return y;
	}

	public FontData getFontData() {

		return fontData;
	}

	public LabelBounds getBounds() {

		return bounds;
	}

	public void setBounds(LabelBounds bounds) {

		this.bounds = bounds;
	}
}
