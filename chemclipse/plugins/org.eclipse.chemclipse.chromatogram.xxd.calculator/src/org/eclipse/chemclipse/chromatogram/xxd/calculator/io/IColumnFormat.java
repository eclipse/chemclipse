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
package org.eclipse.chemclipse.chromatogram.xxd.calculator.io;

public interface IColumnFormat {

	String HEADER_VALUE_DELIMITER = "=";
	String RI_VALUE_DELIMITER = " ";
	//
	String COLUMN_MARKER = "#";
	//
	String COLUMN_NAME = "#COLUMN_NAME";
	String COLUMN_LENGTH = "#COLUMN_LENGTH";
	String COLUMN_DIAMETER = "#COLUMN_DIAMETER";
	String COLUMN_PHASE = "#COLUMN_PHASE";
}
