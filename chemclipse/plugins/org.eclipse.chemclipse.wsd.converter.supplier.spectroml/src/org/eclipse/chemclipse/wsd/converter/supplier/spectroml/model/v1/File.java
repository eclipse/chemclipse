/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.supplier.spectroml.model.v1;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class File {

	@XmlElement(name = "title")
	private String title;
	@XmlElement(name = "timeStamp")
	private TimeStamp timeStamp;
	@XmlElement(name = "path")
	private List<String> pathList;
	@XmlElement(name = "comment")
	private String comment;
	@XmlAttribute(name = "experimentLinks")
	private String experimentLinks;
	@XmlAttribute(name = "externalLinks")
	private String externalLinks;

	public String getTitle() {

		return title;
	}

	public void setTitle(String title) {

		this.title = title;
	}

	public TimeStamp getTimeStamp() {

		return timeStamp;
	}

	public void setTimeStamp(TimeStamp timeStamp) {

		this.timeStamp = timeStamp;
	}

	public List<String> getPathList() {

		return pathList;
	}

	public void setPathList(List<String> pathList) {

		this.pathList = pathList;
	}

	public String getComment() {

		return comment;
	}

	public void setComment(String comment) {

		this.comment = comment;
	}

	public String getExperimentLinks() {

		return experimentLinks;
	}

	public void setExperimentLinks(String experimentLinks) {

		this.experimentLinks = experimentLinks;
	}

	public String getExternalLinks() {

		return externalLinks;
	}

	public void setExternalLinks(String externalLinks) {

		this.externalLinks = externalLinks;
	}
}