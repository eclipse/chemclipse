/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - enhance method definition, add readonly support
 *******************************************************************************/
package org.eclipse.chemclipse.processing.methods;

public interface IProcessMethod extends ProcessEntryContainer {

	/**
	 * 
	 * @return returns the methods UUID to identify the method across file-systems
	 */
	String getUUID();

	/**
	 * The name is used to display a label to the user
	 * 
	 * @return the human readable label/name
	 */
	String getName();

	/**
	 * The category is used to group similar methods
	 * 
	 * @return the category
	 */
	String getCategory();

	/**
	 * The operator is the person who has created / currently manages this method
	 * 
	 * @return the operator of the method
	 */
	String getOperator();

	/**
	 * 
	 * @return the human readable description of this method
	 */
	String getDescription();
}