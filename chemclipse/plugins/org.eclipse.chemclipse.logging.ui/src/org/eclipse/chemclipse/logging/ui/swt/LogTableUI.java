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
package org.eclipse.chemclipse.logging.ui.swt;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.chemclipse.logging.ui.ParsedLogEntry;
import org.eclipse.chemclipse.logging.ui.swt.components.LogListUI;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class LogTableUI extends Composite {

	private LogListUI logListUI;

	public LogTableUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	private void createControl() {

		Composite composite = new Composite(this, SWT.NONE);
		composite.setBackground(Colors.WHITE);
		composite.setLayout(new GridLayout(1, true));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		logListUI = createLogTable(composite);
		logListUI.setEditEnabled(false);
	}

	private LogListUI createLogTable(Composite parent) {

		LogListUI logListUI = new LogListUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);
		logListUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		return logListUI;
	}

	public void setContent(List<String> lines) {

		String regex = "^(\\d{4}-\\d{2}-\\d{2}) (\\d{2}:\\d{2}:\\d{2},\\d{3}) ([A-Z]*) +\\[(.*?)\\] ([A-Z.a-z]*): (.*)$";
		Pattern compiledPattern = Pattern.compile(regex);
		for(String line : lines) {
			Matcher regexMatcher = compiledPattern.matcher(line);
			if(regexMatcher.matches() && regexMatcher.groupCount() == 6) {
				String date = regexMatcher.group(1);
				String time = regexMatcher.group(2);
				String level = regexMatcher.group(3);
				String thread = regexMatcher.group(4);
				String location = regexMatcher.group(5);
				String message = regexMatcher.group(6);
				logListUI.add(new ParsedLogEntry(date, time, level, thread, location, message));
			}
		}
	}
}
