/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scanremover.settings;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scanremover.preferences.PreferenceSupplier;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class ScanRemoverPattern {

	private List<Boolean> patternRemove;
	private int position;

	public ScanRemoverPattern(String scanRemoverPattern) {
		/*
		 * A null string is not allowed.
		 */
		if(scanRemoverPattern == null) {
			scanRemoverPattern = "";
		}
		init(scanRemoverPattern);
	}

	/**
	 * Returns whether the actual element shall be removed or not.
	 * Each call of this method will proceed to the next element.
	 * 
	 * @return boolean
	 */
	public boolean remove() {

		int index = getActualIndex();
		position++;
		boolean removeScan = false;
		/*
		 * Test if the list has an element. In all other cases return false.
		 */
		if(patternRemove.size() > 0) {
			removeScan = patternRemove.get(index);
		}
		return removeScan;
	}

	// ---------------------------------------private methods
	/**
	 * Initialize the remover pattern.
	 */
	private void init(String scanRemoverPattern) {

		/*
		 * Creates a new remover pattern list.
		 * The position is necessary to start at beginning of the list if the end has reached.
		 */
		patternRemove = new ArrayList<Boolean>();
		position = 0;
		scanRemoverPattern = scanRemoverPattern.toUpperCase();
		char[] chars = scanRemoverPattern.toCharArray();
		char removerChar;
		/*
		 * Parse the char array and add only valid elements, skip all others.
		 */
		for(int i = 0; i < chars.length; i++) {
			removerChar = chars[i];
			if(removerChar == PreferenceSupplier.PRESERVE_SIGN) {
				patternRemove.add(false);
			} else {
				if(removerChar == PreferenceSupplier.REMOVE_SIGN) {
					patternRemove.add(true);
				}
			}
		}
	}

	/**
	 * Returns the actual patternRemove list index.
	 * If the position has reached the end it will start at the beginning.
	 * 
	 * @return int
	 */
	private int getActualIndex() {

		/*
		 * The position shall not exceed the ranges of the list.
		 */
		if(position < 0 || position >= patternRemove.size()) {
			position = 0;
		}
		return position;
	}
	// ---------------------------------------private methods
}
