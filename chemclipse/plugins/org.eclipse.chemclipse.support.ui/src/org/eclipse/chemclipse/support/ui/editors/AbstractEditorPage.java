/*******************************************************************************
 * Copyright (c) 2016 Lablicate UG (haftungsbeschränkt).
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public abstract class AbstractEditorPage {

	protected static final int HORIZONTAL_INDENT = 15;
	//
	private FormToolkit toolkit;
	private Composite control;

	public AbstractEditorPage(String pageName, Composite container) {
		/*
		 * Create the parent composite.
		 */
		control = new Composite(container, SWT.NONE);
		control.setLayout(new FillLayout());
		control.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		/*
		 * Forms API
		 */
		toolkit = new FormToolkit(control.getDisplay());
		ScrolledForm scrolledForm = toolkit.createScrolledForm(control);
		scrolledForm.setText(pageName);
		fillBody(scrolledForm);
	}

	/**
	 * Override this method.
	 * 
	 * @param scrolledForm
	 */
	public void fillBody(ScrolledForm scrolledForm) {

	}

	public FormToolkit getFormToolkit() {

		return toolkit;
	}

	public Composite getControl() {

		return control;
	}

	public void dispose() {

		if(toolkit != null) {
			toolkit.dispose();
		}
	}

	protected TableWrapLayout createFormTableWrapLayout(boolean makeColumnsEqualWidth, int numColumns) {

		TableWrapLayout tableWrapLayout = new TableWrapLayout();
		//
		tableWrapLayout.topMargin = 12;
		tableWrapLayout.bottomMargin = 12;
		tableWrapLayout.leftMargin = 6;
		tableWrapLayout.rightMargin = 6;
		tableWrapLayout.horizontalSpacing = 20;
		tableWrapLayout.verticalSpacing = 17;
		tableWrapLayout.makeColumnsEqualWidth = makeColumnsEqualWidth;
		tableWrapLayout.numColumns = numColumns;
		//
		return tableWrapLayout;
	}

	protected Section createSection(Composite parent, int colspan) {

		Section section = toolkit.createSection(parent, Section.EXPANDED);
		//
		section.clientVerticalSpacing = 2;
		section.setLayout(createSectionTableWrapLayout(true, 1));
		TableWrapData tableWrapData = new TableWrapData(TableWrapData.FILL_GRAB);
		tableWrapData.colspan = colspan;
		section.setLayoutData(tableWrapData);
		//
		return section;
	}

	protected Composite createClientInfo(Section section) {

		Composite client = toolkit.createComposite(section, SWT.WRAP);
		//
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginWidth = 2;
		gridLayout.marginHeight = 2;
		gridLayout.marginTop = 2;
		gridLayout.marginLeft = 6;
		client.setLayout(gridLayout);
		//
		return client;
	}

	protected Label createLabel(Composite client, String text) {

		Label label = toolkit.createLabel(client, text);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		label.setLayoutData(gridData);
		return label;
	}

	protected TableWrapLayout createSectionTableWrapLayout(boolean makeColumnsEqualWidth, int numColumns) {

		TableWrapLayout tableWrapLayout = new TableWrapLayout();
		//
		tableWrapLayout.topMargin = 2;
		tableWrapLayout.bottomMargin = 2;
		tableWrapLayout.leftMargin = 2;
		tableWrapLayout.rightMargin = 2;
		tableWrapLayout.horizontalSpacing = 0;
		tableWrapLayout.verticalSpacing = 0;
		tableWrapLayout.makeColumnsEqualWidth = makeColumnsEqualWidth;
		tableWrapLayout.numColumns = numColumns;
		//
		return tableWrapLayout;
	}

	protected Section createSection(Composite parent, int colspan, String text, String description) {

		Section section = toolkit.createSection(parent, Section.EXPANDED | Section.TWISTIE | Section.DESCRIPTION | Section.TITLE_BAR);
		//
		section.clientVerticalSpacing = 2;
		section.setText(text);
		section.setDescription(description);
		section.setLayout(createSectionTableWrapLayout(true, 1));
		TableWrapData tableWrapData = new TableWrapData(TableWrapData.FILL_GRAB);
		tableWrapData.colspan = colspan;
		section.setLayoutData(tableWrapData);
		//
		return section;
	}

	protected Composite createClient(Section section) {

		Composite client = toolkit.createComposite(section, SWT.WRAP);
		//
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginWidth = 2;
		gridLayout.marginHeight = 2;
		gridLayout.marginTop = 10;
		gridLayout.marginLeft = 15;
		client.setLayout(gridLayout);
		//
		return client;
	}
}
