/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v12.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <pre>
 *        The format of the run - This allows the software to display the data
 *        according to the qPCR instrument run format.
 *        Rotor formats always have 1 column; rows correspond to the number
 *        of places in the rotor.
 *        
 *        Values for common formats are:
 *        
 *        Format            |    rows    |   columns   |  rowLabel  | columnLabel
 *        --------------------------------------------------------------------------
 *        single-well       |     1      |      1      |    123     |     123
 *        48-well plate     |     6      |      8      |    ABC     |     123
 *        96-well plate     |     8      |     12      |    ABC     |     123
 *        384-well plate    |    16      |     24      |    ABC     |     123
 *        1536-well plate   |    32      |     48      |    ABC     |     123
 *        3072-well array   |    32      |     96      |   A1a1     |    A1a1
 *        5184-well chip    |    72      |     72      |    ABC     |     123
 *        32-well rotor     |    32      |      1      |    123     |     123
 *        72-well rotor     |    72      |      1      |    123     |     123
 *        100-well rotor    |   100      |      1      |    123     |     123
 *        free format       |    -1      |      1      |    123     |     123
 *        
 *        If rows are -1 then the software should not try to reconstruct a plate and 
 *        just display all react data in list (1 column) form.
 *        
 *        If columns is 1 then the software should not display a column label.
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pcrFormatType", propOrder = {"rows", "columns", "rowLabel", "columnLabel"})
public class PcrFormatType {

	protected int rows;
	protected int columns;
	@XmlElement(required = true)
	protected String rowLabel;
	@XmlElement(required = true)
	protected String columnLabel;

	public int getRows() {

		return rows;
	}

	public void setRows(int value) {

		this.rows = value;
	}

	public int getColumns() {

		return columns;
	}

	public void setColumns(int value) {

		this.columns = value;
	}

	public String getRowLabel() {

		return rowLabel;
	}

	public void setRowLabel(String value) {

		this.rowLabel = value;
	}

	public String getColumnLabel() {

		return columnLabel;
	}

	public void setColumnLabel(String value) {

		this.columnLabel = value;
	}
}
