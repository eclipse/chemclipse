/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.model;

import org.eclipse.chemclipse.model.literature.LiteratureSupport;

public class LiteratureReference {

	private String content = "";
	private String title = "";
	private String url = "";

	public LiteratureReference(String content) {

		this.content = content;
		this.title = LiteratureSupport.getTitle(content);
		this.url = LiteratureSupport.getContainedDOI(content);
	}

	public String getContent() {

		return content;
	}

	public String getTitle() {

		return title;
	}

	public String getUrl() {

		return url;
	}
}