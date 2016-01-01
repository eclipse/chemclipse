/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.database.model;

import org.eclipse.chemclipse.database.documents.ILibraryDescriptionDocument;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

public interface IDatabase {

	/**
	 * Returns an instance of the currently used database.
	 * The connection will be reopened if it was closed meanwhile.
	 * 
	 * @return {@link ODatabaseDocumentTx}
	 */
	ODatabaseDocumentTx getDB();

	/**
	 * Closes the database.
	 */
	void close();

	/**
	 * Returns the library description document of the database.
	 * 
	 * @return {@link ILibraryDescriptionDocument}
	 */
	ILibraryDescriptionDocument getLibraryDescriptionDocument();

	/**
	 * Returns the number of entries stored in the cluster name or 0 if the cluster not exists.
	 * 
	 * @param clusterName
	 * @return long
	 */
	long countCluster(String clusterName);

	ILibraryDescriptionDocument getLibraryDescriptionDocumentOnSeparateConnection();

	long countClusterOnSeparateConnection(String clusterName);
}
