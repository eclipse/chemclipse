/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.logging.ui;

public class ParsedLogEntry {

	private String date;
	private String time;
	private String level;
	private String thread;
	private String location;
	private String message;

	public ParsedLogEntry(String date, String time, String level, String thread, String location, String message) {

		this.date = date;
		this.time = time;
		this.level = level;
		this.thread = thread;
		this.location = location;
		this.message = message;
	}

	public String getDate() {

		return date;
	}

	public void setDate(String date) {

		this.date = date;
	}

	public String getTime() {

		return time;
	}

	public void setTime(String time) {

		this.time = time;
	}

	public String getLevel() {

		return level;
	}

	public void setLevel(String level) {

		this.level = level;
	}

	public String getThread() {

		return thread;
	}

	public void setThread(String thread) {

		this.thread = thread;
	}

	public String getLocation() {

		return location;
	}

	public void setLocation(String location) {

		this.location = location;
	}

	public String getMessage() {

		return message;
	}

	public void setMessage(String message) {

		this.message = message;
	}
}
