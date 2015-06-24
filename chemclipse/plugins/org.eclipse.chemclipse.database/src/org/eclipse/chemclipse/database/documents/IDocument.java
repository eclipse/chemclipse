/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.database.documents;

import java.util.Set;

import com.orientechnologies.orient.core.record.ORecordAbstract;
import com.orientechnologies.orient.core.record.impl.ODocument;

public interface IDocument {

	String SINGLE_QUOTE = "'";
	String ESCAPED_SINGLE_QUOTE = "__SINGLEQUOTE__";

	ORecordAbstract save();

	void setFields();

	/**
	 * This method may return null.
	 * 
	 * @return {@link ODocument}
	 */
	ODocument getDocument();

	/**
	 * Returns the id of the current document.
	 * getIdentity().getClusterPosition();
	 * 
	 * @return long
	 */
	long getDocumentId();

	/**
	 * Returns the id of the field. If there is no field, -1 will be returned.
	 * 
	 * @param fieldName
	 * @return long
	 */
	long getId(String fieldName);

	/**
	 * Returns a set of long ids for the given field name.
	 * 
	 * @param fieldName
	 * @return Set<Long>
	 */
	Set<Long> getIds(String fieldName);

	/**
	 * Returns a string contained in the given field name.
	 * If the field is not available, "" will be returned.
	 * 
	 * @param fieldName
	 * @return
	 */
	String getString(String fieldName);

	/**
	 * Returns an integer value of the given field or -1 if it not exists.
	 * 
	 * @param fieldName
	 * @return int
	 */
	int getInteger(String fieldName);

	/**
	 * Returns a float value of the given field or -1 if it not exists.
	 * 
	 * @param fieldName
	 * @return float
	 */
	float getFloat(String fieldName);

	/**
	 * Returns a double value of the given field or -1 if it not exists.
	 * 
	 * @param fieldName
	 * @return double
	 */
	double getDouble(String fieldName);

	/**
	 * Returns a long value of the given field or -1 if it not exists.
	 * 
	 * @param fieldName
	 * @return long
	 */
	long getLong(String fieldName);

	/**
	 * Returns null if the document or the field is null.
	 * 
	 * @param fieldName
	 * @return object
	 */
	Object getObject(String fieldName);

	/**
	 * Returns a boolean value of the given field.
	 * 
	 * @param fieldName
	 * @return boolean
	 */
	boolean getBoolean(String fieldName);
}
