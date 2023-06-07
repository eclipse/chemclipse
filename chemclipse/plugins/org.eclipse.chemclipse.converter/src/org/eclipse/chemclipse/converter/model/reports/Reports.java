/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
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

import java.util.ArrayList;

public class Reports<T extends IReport<?>> extends ArrayList<T> implements IReports<T> {

	/**
	 * Renew this UUID on change.
	 */
	private static final long serialVersionUID = 1607064289185934480L;
	/*
	 * It would be better to extend from AbstractFileAttributes,
	 * but it's not possible to extend from two classes.
	 */
	private String fileName = "";
	private String canonicalPath = "";
	private long creationTime = 0;
	private long lastAccessTime = 0;
	private long lastModificationTime = 0;

	@Override
	public String getFileName() {

		return fileName;
	}

	@Override
	public void setFileName(String fileName) {

		this.fileName = fileName;
	}

	@Override
	public String getCanonicalPath() {

		return canonicalPath;
	}

	@Override
	public void setCanonicalPath(String canonicalPath) {

		this.canonicalPath = canonicalPath;
	}

	@Override
	public long getCreationTime() {

		return creationTime;
	}

	@Override
	public void setCreationTime(long creationTime) {

		this.creationTime = creationTime;
	}

	@Override
	public long getLastAccessTime() {

		return lastAccessTime;
	}

	@Override
	public void setLastAccessTime(long lastAccessTime) {

		this.lastAccessTime = lastAccessTime;
	}

	@Override
	public long getLastModificationTime() {

		return lastModificationTime;
	}

	@Override
	public void setLastModificationTime(long lastModificationTime) {

		this.lastModificationTime = lastModificationTime;
	}

	/**
	 * May return null.
	 * 
	 * @param number
	 * @return Report
	 */
	@Override
	public T getReport(int number) {

		T report = null;
		if(size() > 0) {
			for(int i = 0; i < size(); i++) {
				report = get(i);
				if(report.getReportNumber() == number) {
					return report;
				}
			}
		}
		return report;
	}

	/**
	 * Returns the report with the highest number.
	 * May return null.
	 * 
	 * @param number
	 * @return Report
	 */
	@Override
	public T getReportFinal() {

		int number = Integer.MIN_VALUE;
		T reportFinal = null;
		if(size() > 0) {
			for(int i = 0; i < size(); i++) {
				T report = get(i);
				int reportNumber = report.getReportNumber();
				if(reportNumber > number) {
					number = reportNumber;
					reportFinal = report;
				}
			}
		}
		return reportFinal;
	}
}
