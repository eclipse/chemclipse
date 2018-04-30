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
package org.eclipse.chemclipse.ux.extension.ui.support;

import javax.naming.directory.InvalidAttributesException;

public class AbstractMeasurementResultTitles implements IMeasurementResultTitles {

	private String[] titles = new String[]{};
	private int[] bounds = new int[]{};

	public AbstractMeasurementResultTitles(String[] titles, int[] bounds) throws InvalidAttributesException {
		/*
		 * Checks
		 */
		if(titles == null) {
			throw new InvalidAttributesException("Titles must be not null.");
		}
		//
		if(bounds == null) {
			throw new InvalidAttributesException("Bounds must be not null.");
		}
		//
		if(titles.length == 0) {
			throw new InvalidAttributesException("The element length must be > 0.");
		}
		//
		if(titles.length != bounds.length) {
			throw new InvalidAttributesException("Titles and Bounds must have the same length.");
		}
		//
		this.titles = titles;
		this.bounds = bounds;
	}

	@Override
	public String[] getTitles() {

		return titles;
	}

	@Override
	public int[] getBounds() {

		return bounds;
	}
}
