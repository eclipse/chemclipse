/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.connector.supplier.microsoft.office.ui.editors;

import java.io.File;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleControlSite;
import org.eclipse.swt.ole.win32.OleEvent;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.ole.win32.OleListener;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.EditorPart;

public abstract class OLEEditor extends EditorPart {

	private FormToolkit formToolkit;
	private OleControlSite oleControlSite;
	private File officeFile;
	private String progId; // e.g. "Excel.Sheet";
	private String filterExtension; // e.g. "xlsx";
	private String filterName; // e.g. "Microsoft Excel 2007 (*.xlsx)";
	/*
	 * Mark editor as dirty if something has changed.
	 */
	private boolean isDirty = false;

	public OLEEditor(String progId, String filterExtension, String filterName) {
		this.progId = progId;
		this.filterExtension = filterExtension;
		this.filterName = filterName;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {

		if(isOleEditorActive()) {
			oleControlSite.save(officeFile, true);
			isDirty = false;
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}

	@Override
	public void doSaveAs() {

		if(isOleEditorActive()) {
			Shell shell = Display.getCurrent().getActiveShell();
			FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
			fileDialog.setFilterExtensions(new String[]{filterExtension});
			fileDialog.setFilterNames(new String[]{filterName});
			String filePath;
			filePath = fileDialog.open();
			if(filePath != null) {
				File officeFileNew = new File(filePath);
				if(isOleEditorActive()) {
					oleControlSite.save(officeFileNew, true);
				}
			}
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {

		setSite(site);
		setInput(input);
		setPartName(input.getName());
		if(input instanceof IPathEditorInput) {
			officeFile = ((IPathEditorInput)input).getPath().toFile();
		}
	}

	@Override
	public boolean isDirty() {

		return isDirty;
	}

	/**
	 * Sets the editor dirty.
	 */
	protected void setDirty() {

		this.isDirty = true;
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	@Override
	public boolean isSaveAsAllowed() {

		return true;
	}

	@Override
	public void createPartControl(Composite parent) {

		parent.setLayout(new FillLayout());
		if(OperatingSystemUtils.isWindows()) {
			Composite oleComposite = new Composite(parent, SWT.NONE);
			oleComposite.setLayout(new FillLayout());
			OleFrame oleFrame = new OleFrame(oleComposite, SWT.NONE);
			try {
				oleControlSite = new OleControlSite(oleFrame, SWT.NONE, progId, officeFile);
				registerOfficeDocumentChangeListener(oleControlSite);
			} catch(Exception e) {
				/*
				 * Hide the oleFrame and show the info instead.
				 */
				oleControlSite = null;
				oleFrame.dispose();
				createInfo(oleComposite);
			}
		} else {
			createInfo(parent);
		}
	}

	private void registerOfficeDocumentChangeListener(OleControlSite controlSite) {

		// controlSite.doVerb(OLE.OLEIVERB_INPLACEACTIVATE);
		OleAutomation oleAutomationDocument = new OleAutomation(controlSite);
		int[] dispIDs = oleAutomationDocument.getIDsOfNames(new String[]{"Application"});
		Variant variant = oleAutomationDocument.getProperty(dispIDs[0]);
		OleAutomation oleAutomationApplication = variant.getAutomation();
		variant.dispose();
		oleAutomationDocument.dispose();
		/*
		 * The listener detects whenever something changes in the office file.
		 */
		OleListener listener = new OleListener() {

			public void handleEvent(OleEvent e) {

				setDirty();
			}
		};
		/*
		 * Register the events to be checked.
		 * EXCEL
		 */
		if(progId.equals(IOLEApplication.PROG_ID_EXCEL)) {
			controlSite.addEventListener(oleAutomationApplication, IOLEApplication.GUID_ATTRIBUTE_EXCEL, IOLEApplication.EXCEL_NEW_WORKBOOK, listener);
			controlSite.addEventListener(oleAutomationApplication, IOLEApplication.GUID_ATTRIBUTE_EXCEL, IOLEApplication.EXCEL_SHEET_ACTIVATE, listener);
			controlSite.addEventListener(oleAutomationApplication, IOLEApplication.GUID_ATTRIBUTE_EXCEL, IOLEApplication.EXCEL_SHEET_SELECTION_CHANGE, listener);
		}
		/*
		 * WORD
		 */
		if(progId.equals(IOLEApplication.PROG_ID_WORD)) {
			controlSite.addEventListener(oleAutomationApplication, IOLEApplication.GUID_ATTRIBUTE_WORD, IOLEApplication.WORD_DOCUMENT_CHANGE, listener);
			controlSite.addEventListener(oleAutomationApplication, IOLEApplication.GUID_ATTRIBUTE_WORD, IOLEApplication.WORD_WINDOW_ACTIVATE, listener);
			controlSite.addEventListener(oleAutomationApplication, IOLEApplication.GUID_ATTRIBUTE_WORD, IOLEApplication.WORD_WINDOW_SELECTION_CHANGE, listener);
		}
	}

	private void createInfo(Composite parent) {

		/*
		 * Create the parent composite.
		 */
		parent.setLayout(new FillLayout());
		parent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		/*
		 * Forms API
		 */
		formToolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm scrolledForm = formToolkit.createScrolledForm(parent);
		Composite scrolledFormComposite = scrolledForm.getBody();
		formToolkit.decorateFormHeading(scrolledForm.getForm());
		scrolledFormComposite.setLayout(new TableWrapLayout());
		scrolledForm.setText("Microsoft Office Editor");
		/*
		 * Sections
		 */
		createProblemSection(scrolledFormComposite);
		createSolutionSection(scrolledFormComposite);
	}

	private void createProblemSection(Composite parent) {

		Section section;
		Composite client;
		GridLayout layout;
		/*
		 * Section Problem
		 */
		section = formToolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText("Problem");
		section.setDescription("There has gone something wrong to open the editor.");
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		/*
		 * Client
		 */
		client = formToolkit.createComposite(section, SWT.WRAP);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		client.setLayout(layout);
		/*
		 * Labels
		 */
		if(OperatingSystemUtils.isWindows()) {
			createLabel(client, "The editor couldn't be started.", IApplicationImage.IMAGE_WARN);
			createLabel(client, "Is an office version already installed?", IApplicationImage.IMAGE_QUESTION);
			createLabel(client, "Have you installed the latest office version?", IApplicationImage.IMAGE_QUESTION);
			createLabel(client, "Have you tried *.xls and *.doc instead of *.xlsx and *.docx formats?", IApplicationImage.IMAGE_QUESTION);
			createLabel(client, "Are you able to open the office file directly?", IApplicationImage.IMAGE_QUESTION);
		} else {
			createLabel(client, "You're not using Microsoft Windows. Read the solution section.", IApplicationImage.IMAGE_WARN);
		}
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		formToolkit.paintBordersFor(client);
	}

	private void createSolutionSection(Composite parent) {

		Section section;
		Composite client;
		GridLayout layout;
		GridData gridData;
		/*
		 * Section Problem
		 */
		section = formToolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText("Solution");
		section.setDescription("You may chose another office package.");
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		/*
		 * Client
		 */
		client = formToolkit.createComposite(section, SWT.WRAP);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		client.setLayout(layout);
		/*
		 * Labels and Links
		 */
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		createLabel(client, "There are some free office alternatives available:", IApplicationImage.IMAGE_INFO);
		createLink(client, "LibreOffice", "http://www.libreoffice.org");
		createLink(client, "OpenOffice", "http://www.openoffice.org");
		createLabel(client, "You can use the office package in combination with the NOA4e plug-in:", IApplicationImage.IMAGE_INFO);
		createLink(client, "NOA4e plug-in", "http://ubion.ion.ag/loesungen/003officeintegrationeditor");
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		formToolkit.paintBordersFor(client);
	}

	private void createLabel(Composite parent, String text, String image) {

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		CLabel label = new CLabel(parent, SWT.LEFT);
		label.setText(text);
		if(image != null) {
			label.setImage(ApplicationImageFactory.getInstance().getImage(image, IApplicationImage.SIZE_16x16));
		}
		label.setLayoutData(gridData);
	}

	private void createLink(Composite parent, String text, final String url) {

		Link link = new Link(parent, SWT.NONE);
		link.setText(text + " (<a>" + url + "</a>)");
		link.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				Program.launch(url);
			}
		});
	}

	@Override
	public void setFocus() {

		if(isOleEditorActive()) {
			oleControlSite.setFocus();
		}
	}

	private boolean isOleEditorActive() {

		return (oleControlSite != null) ? true : false;
	}

	@Override
	public void dispose() {

		if(formToolkit != null) {
			formToolkit.dispose();
		}
	}
}
