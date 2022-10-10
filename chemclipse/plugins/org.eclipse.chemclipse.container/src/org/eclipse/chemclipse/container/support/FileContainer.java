/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.container.support;

import org.eclipse.chemclipse.container.definition.IFileContentProvider;
import org.eclipse.chemclipse.converter.core.IMagicNumberMatcher;

public class FileContainer {

	private String fileExtension = "";
	private IMagicNumberMatcher magicNumberMatcher = null;
	private IFileContentProvider contentProvider = null;

	public String getFileExtension() {

		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {

		this.fileExtension = fileExtension;
	}

	public IMagicNumberMatcher getMagicNumberMatcher() {

		return magicNumberMatcher;
	}

	public void setMagicNumberMatcher(IMagicNumberMatcher magicNumberMatcher) {

		this.magicNumberMatcher = magicNumberMatcher;
	}

	public IFileContentProvider getContentProvider() {

		return contentProvider;
	}

	public void setContentProvider(IFileContentProvider contentProvider) {

		this.contentProvider = contentProvider;
	}
}
