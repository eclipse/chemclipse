/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.io.support;

public interface IStructureResolver {

	/**
	 * Formats a integer value e.g. like this:
	 * 0 1 0 0 0 0 0 0 | 0 1 0 0 0 1 0 0 | 1 1 0 1 0 0 1 0 | 1 1 0 1 1 0 0 1
	 * 
	 * @param value
	 * @return String
	 */
	String formatAsBinary(int value);

	/**
	 * Formats a long value e.g. like this:
	 * 0 1 0 0 0 0 0 0 | 0 1 0 0 0 1 0 0 | 1 1 0 1 0 0 1 0 | ...
	 * 
	 * @param value
	 * @return String
	 */
	String formatAsBinary(long value);

	String formatAsBinary(float value);

	String formatAsBinary(double value);
}
