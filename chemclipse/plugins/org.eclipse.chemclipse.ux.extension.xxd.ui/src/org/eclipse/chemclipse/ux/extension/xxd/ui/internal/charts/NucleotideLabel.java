/*******************************************************************************
 * Copyright (c) 2020, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - initial API and implementation
 * Matthias Mailänder - adapted for nucleotide display
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts;

import org.eclipse.chemclipse.dsd.model.core.Nucleobase;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.NucleotideSupport;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

public class NucleotideLabel {

	private final Nucleobase nucleobase;
	private final String id;
	private final boolean isActive;
	private double x;
	private double y;
	private final Font font;
	/*
	 * Cached values used for calculation
	 */
	private LabelBounds bounds;

	public NucleotideLabel(Nucleobase nucleobase, String id, Font font, boolean isActive, double x, double y) {

		this.nucleobase = nucleobase;
		this.id = id;
		this.font = font;
		this.isActive = isActive;
		this.x = x;
		this.y = y;
	}

	public String getLabel() {

		return String.valueOf(nucleobase.letter());
	}

	public Color getColor() {

		return NucleotideSupport.getColor(nucleobase);
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

	/**
	 * Adjust the y value.
	 * Handle this method carefully.
	 * 
	 * @param x
	 */
	public void adjustX(double x) {

		this.x = x;
	}

	public double getY() {

		return y;
	}

	/**
	 * Adjust the y value.
	 * Handle this method carefully.
	 * 
	 * @param y
	 */
	public void adjustY(double y) {

		this.y = y;
	}

	public Font getFont() {

		return font;
	}

	public LabelBounds getBounds() {

		return bounds;
	}

	public void setBounds(LabelBounds bounds) {

		this.bounds = bounds;
	}
}