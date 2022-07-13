/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.report.supplier.tabular.model;

public enum LogicalOperator {

	AND, //
	OR;

	public static LogicalOperator parse(String input) {

		if(input.contains("&")) {
			return LogicalOperator.AND;
		}
		if(input.contains("/")) {
			return LogicalOperator.OR;
		}
		return null;
	}
}