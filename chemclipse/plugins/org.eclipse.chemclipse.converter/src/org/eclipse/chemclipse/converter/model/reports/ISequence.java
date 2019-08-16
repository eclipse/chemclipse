/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.model.reports;

import java.util.List;

public interface ISequence<T extends ISequenceRecord> extends List<T>, IFileAttributes {

	String getSequenceId();

	void setSequenceId(String sequenceId);

	String getInfo();

	void setInfo(String info);

	String getMethod();

	void setMethod(String method);

	String getDataPath();

	void setDataPath(String dataPath);
}