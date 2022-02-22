/*******************************************************************************
 * Copyright (c) 2011, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.preferences.PreferenceSupplier;

public class ScanRemoverPattern {

	private List<Boolean> patternRemove = new ArrayList<>();
	private int position = 0;

	public ScanRemoverPattern(String scanRemoverPattern) {

		init(scanRemoverPattern == null ? "" : scanRemoverPattern);
	}

	/**
	 * Returns whether the actual element shall be removed or not.
	 * Each call of this method will step to the next element of
	 * the given pattern.
	 * 
	 * @return boolean
	 */
	public boolean remove() {

		/*
		 * Test if the list has an element. In all other cases return false.
		 */
		boolean removeScan = false;
		if(!patternRemove.isEmpty()) {
			removeScan = patternRemove.get(getCurrentIndex());
			position++;
		}
		//
		return removeScan;
	}

	/**
	 * Initialize the remover pattern.
	 */
	private void init(String scanRemoverPattern) {

		/*
		 * Creates a new remover pattern list.
		 * The position is necessary to start at beginning of the list if the end has reached.
		 */
		scanRemoverPattern = scanRemoverPattern.toUpperCase();
		char[] chars = scanRemoverPattern.toCharArray();
		/*
		 * Parse the char array and add only valid elements, skip all others.
		 */
		for(int i = 0; i < chars.length; i++) {
			char charSign = chars[i];
			if(PreferenceSupplier.PRESERVE_SIGN.equals(charSign)) {
				patternRemove.add(false);
			} else if(PreferenceSupplier.REMOVE_SIGN.equals(charSign)) {
				patternRemove.add(true);
			}
		}
	}

	/**
	 * Returns the actual patternRemove list index.
	 * If the position has reached the end it will start at the beginning.
	 * 
	 * @return int
	 */
	private int getCurrentIndex() {

		/*
		 * The position shall not exceed the ranges of the list.
		 */
		if(position < 0 || position >= patternRemove.size()) {
			position = 0;
		}
		//
		return position;
	}
}