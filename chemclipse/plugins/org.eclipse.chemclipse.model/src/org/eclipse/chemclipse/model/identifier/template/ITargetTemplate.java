/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier.template;

public interface ITargetTemplate {

	String getName();

	void setName(String name);

	String getCasNumber();

	void setCasNumber(String casNumber);

	String getComments();

	void setComments(String comments);

	String getContributor();

	void setContributor(String contributor);

	String getReferenceId();

	void setReferenceId(String referenceId);
}