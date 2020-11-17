/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.services.IMoleculeImageService;
import org.eclipse.chemclipse.swt.ui.services.ImageServiceInput;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageScans;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class ExtendedMoleculeUI extends Composite implements IExtendedPartUI {

	private static final Logger logger = Logger.getLogger(ExtendedMoleculeUI.class);
	//
	private static final String ERROR_MESSAGE = "The molecule image couldn't be created.";
	private static final ILibraryInformation LIBRARY_INFORMATION_THIAMIN = createLibraryInformationDefault();
	private static final String THIAMINE_NAME = "Thiamine";
	private static final String THIAMINE_CAS = "70-16-6";
	private static final String THIAMINE_SMILES = "OCCc1c(C)[n+](=cs1)Cc2cnc(C)nc(N)2";
	//
	private static final double SCALE_DEFAULT = 1.0d;
	private static final double SCALE_DELTA = 0.1d;
	//
	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private Button buttonToolbarEdit;
	private AtomicReference<Composite> toolbarEdit = new AtomicReference<>();
	private TabFolder tabFolder;
	private ComboViewer comboViewerServices;
	private Text textInput;
	private ComboViewer comboViewerInput;
	private Canvas canvasMolecule;
	private Text textMolecule;
	//
	private ILibraryInformation libraryInformation = LIBRARY_INFORMATION_THIAMIN;
	//
	private double scaleFactor = SCALE_DEFAULT;
	private Image imageMolecule = null;
	//
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public ExtendedMoleculeUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setInput(ILibraryInformation libraryInformation) {

		this.libraryInformation = libraryInformation;
		updateContent(getDisplay());
	}

	@Override
	protected void finalize() throws Throwable {

		if(imageMolecule != null) {
			imageMolecule.dispose();
		}
		super.finalize();
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createToolbarInfo(this);
		createToolbarEdit(this);
		createTabFolderSection(this);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfo, buttonToolbarInfo, IMAGE_INFO, TOOLTIP_INFO, true);
		enableToolbar(toolbarEdit, buttonToolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT, false);
		/*
		 * Services
		 */
		Object[] moleculeImageServices = Activator.getDefault().getMoleculeImageServices();
		comboViewerServices.setInput(moleculeImageServices);
		if(moleculeImageServices.length >= 1) {
			comboViewerServices.getCombo().select(0);
		}
		/*
		 * Input Types
		 */
		comboViewerInput.setInput(ImageServiceInput.values());
		comboViewerInput.getCombo().select(0);
		/*
		 * Create the image
		 */
		updateContent(getDisplay());
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(6, false));
		//
		buttonToolbarInfo = createButtonToggleToolbar(composite, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
		buttonToolbarEdit = createButtonToggleToolbar(composite, toolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT);
		comboViewerServices = createComboViewerServices(composite);
		createButtonReset(composite);
		createButtonExport(composite);
		createSettingsButton(composite);
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private void createToolbarEdit(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		textInput = createTextInput(composite);
		comboViewerInput = createComboViewerInput(composite);
		createButtonCalculate(composite);
		//
		toolbarEdit.set(composite);
	}

	private Text createTextInput(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setText(THIAMINE_NAME);
		text.setToolTipText("Input");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(isEnterPressed(e)) {
					libraryInformation = createLibraryInformationByInput();
					updateContent(e.display);
				}
			}
		});
		//
		return text;
	}

	private ComboViewer createComboViewerInput(Composite composite) {

		ComboViewer comboViewer = new ComboViewer(composite, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof ImageServiceInput) {
					ImageServiceInput imageServiceInput = (ImageServiceInput)element;
					return imageServiceInput.getLabel();
				}
				//
				return null;
			}
		});
		/*
		 * Select the item.
		 */
		combo.setToolTipText("Select an image service input type.");
		GridData gridData = new GridData();
		gridData.widthHint = 80;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setTextInput();
			}
		});
		//
		return comboViewer;
	}

	private Button createButtonCalculate(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Calculate the molecule image.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CALCULATE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				libraryInformation = createLibraryInformationByInput();
				updateContent(e.display);
			}
		});
		//
		return button;
	}

	private void createTabFolderSection(Composite parent) {

		tabFolder = new TabFolder(parent, SWT.BOTTOM);
		tabFolder.setBackgroundMode(SWT.INHERIT_DEFAULT);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		canvasMolecule = createMoleculeImage(tabFolder);
		textMolecule = createMoleculeContent(tabFolder);
	}

	private Canvas createMoleculeImage(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Molecule");
		//
		Composite composite = new Composite(tabFolder, SWT.BORDER);
		composite.setLayout(new FillLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		Canvas canvas = createCanvasMolecule(composite);
		tabItem.setControl(composite);
		//
		return canvas;
	}

	private Canvas createCanvasMolecule(Composite parent) {

		Canvas canvas = new Canvas(parent, SWT.FILL);
		canvas.setBackground(Colors.WHITE);
		//
		canvas.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(ControlEvent e) {

				canvas.redraw();
			}
		});
		//
		canvas.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseScrolled(MouseEvent event) {

				scaleFactor += (event.count > 0) ? SCALE_DELTA : -SCALE_DELTA;
				canvas.redraw();
			}
		});
		//
		canvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent event) {

				drawImage(canvas, event);
			}
		});
		//
		return canvas;
	}

	private Text createMoleculeContent(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Content");
		//
		Text text = createTextMolecule(tabFolder);
		tabItem.setControl(text);
		//
		return text;
	}

	private Text createTextMolecule(Composite parent) {

		Text text = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		text.setText("");
		text.setToolTipText("Molecule Structure");
		//
		return text;
	}

	private ComboViewer createComboViewerServices(Composite composite) {

		ComboViewer comboViewer = new ComboViewer(composite, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IMoleculeImageService) {
					IMoleculeImageService moleculeImageService = (IMoleculeImageService)element;
					combo.setToolTipText(moleculeImageService.getDescription());
					return moleculeImageService.getName();
				}
				//
				combo.setToolTipText("");
				return null;
			}
		});
		/*
		 * Select the item.
		 */
		combo.setToolTipText("Select a service to create the molecule image.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 200;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				createMoleculeImage(e.display);
			}
		});
		//
		return comboViewer;
	}

	private Button createButtonReset(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Reset molecule image.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				scaleFactor = SCALE_DEFAULT;
				libraryInformation = LIBRARY_INFORMATION_THIAMIN;
				updateContent(e.display);
			}
		});
		//
		return button;
	}

	private Button createButtonExport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Export the molecule image");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAVE_AS, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(imageMolecule != null) {
					FileDialog fileDialog = new FileDialog(e.display.getActiveShell(), SWT.SAVE);
					fileDialog.setOverwrite(true);
					fileDialog.setText("Save Molecule");
					fileDialog.setFilterExtensions(new String[]{"*.png"});
					fileDialog.setFilterNames(new String[]{"Portable Network Graphics  (*.png)"});
					fileDialog.setFileName(getExportName());
					fileDialog.setFilterPath(preferenceStore.getString(PreferenceConstants.P_MOLECULE_PATH_EXPORT));
					//
					String pathname = fileDialog.open();
					if(pathname != null) {
						preferenceStore.putValue(PreferenceConstants.P_MOLECULE_PATH_EXPORT, fileDialog.getFilterPath());
						ImageData data = imageMolecule.getImageData();
						ImageLoader loader = new ImageLoader();
						loader.data = new ImageData[]{data};
						loader.save(pathname, SWT.IMAGE_PNG);
					}
				}
			}
		});
		//
		return button;
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePageScans.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings(display);
			}
		});
	}

	private void applySettings(Display display) {

		createMoleculeImage(display);
	}

	private boolean isEnterPressed(KeyEvent e) {

		return (e.keyCode == SWT.LF || e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR);
	}

	private void createMoleculeImage(Display display) {

		/*
		 * Dispose an existing image.
		 */
		String moleculeInfo = "";
		if(imageMolecule != null) {
			imageMolecule.dispose();
			imageMolecule = null;
		}
		//
		IMoleculeImageService moleculeImageService = getMoleculeImageService();
		if(moleculeImageService != null) {
			/*
			 * Canvas and a scale factor are used.
			 */
			Point size = calculateImageSize();
			int width = size.x;
			int height = size.y;
			/*
			 * If the library information is null, take the text.
			 */
			if(libraryInformation == null) {
				libraryInformation = new LibraryInformation();
				ImageServiceInput imageInput = getImageInput();
				String text = textInput.getText().trim();
				switch(imageInput) {
					case SMILES:
						libraryInformation.setSmiles(text);
						break;
					default:
						libraryInformation.setName(text);
						break;
				}
			}
			//
			moleculeInfo = getMoleculeInformation(libraryInformation);
			if(isSourceDataAvailable(libraryInformation)) {
				imageMolecule = moleculeImageService.create(display, libraryInformation, width, height);
			} else {
				logger.info(ERROR_MESSAGE);
			}
		}
		//
		toolbarInfo.get().setText(moleculeInfo);
	}

	private Point calculateImageSize() {

		Point size = canvasMolecule.getSize();
		/*
		 * Default 1
		 */
		int width = size.x;
		int height = size.y;
		//
		if(scaleFactor != 1.0d) {
			width += (int)(size.x * scaleFactor);
			height += (int)(size.y * scaleFactor);
		}
		//
		return adjustSize(width, height);
	}

	private Point adjustSize(int width, int height) {

		width = (width <= 0) ? 1 : width;
		height = (height <= 0) ? 1 : height;
		//
		return new Point(width, height);
	}

	private void drawImage(Canvas canvas, PaintEvent event) {

		createMoleculeImage(event.display);
		if(imageMolecule != null) {
			/*
			 * Image
			 */
			Rectangle bounds = imageMolecule.getBounds();
			int srcX = 0;
			int srcY = 0;
			int srcWidth = bounds.width;
			int srcHeight = bounds.height;
			int destX = 0;
			int destY = 0;
			int destWidth = bounds.width;
			int destHeight = bounds.height;
			//
			if(scaleFactor != 1.0d) {
				destWidth = (int)(bounds.width * scaleFactor);
				destHeight = (int)(bounds.height * scaleFactor);
				Point size = canvasMolecule.getSize();
				int corrwidth = (int)(size.x * scaleFactor);
				int corrheight = (int)(size.y * scaleFactor);
				destX = (int)(srcWidth / 2.0d - destWidth / 2.0d - corrwidth / 2.0d);
				destY = (int)(srcHeight / 2.0d - destHeight / 2.0d - corrheight / 2.0d);
			}
			/*
			 * Correction
			 */
			Point destSize = adjustSize(destWidth, destHeight);
			event.gc.drawImage(imageMolecule, srcX, srcY, srcWidth, srcHeight, destX, destY, destSize.x, destSize.y);
		} else {
			/*
			 * Text
			 */
			Font font = getFont();
			FontData[] fontData = font.getFontData();
			int width = event.gc.stringExtent(ERROR_MESSAGE).x;
			int height = fontData[0].getHeight();
			//
			Point size = canvas.getSize();
			int x = (int)(size.x / 2.0d - width / 2.0d);
			int y = (int)(size.y / 2.0d - height / 2.0d);
			event.gc.drawText(ERROR_MESSAGE, x, y, true);
		}
	}

	private boolean isSourceDataAvailable(ILibraryInformation libraryInformation) {

		if(!libraryInformation.getName().isEmpty()) {
			return true;
		} else if(!libraryInformation.getCasNumber().isEmpty()) {
			return true;
		} else if(!libraryInformation.getSmiles().isEmpty()) {
			return true;
		} else if(!libraryInformation.getInChI().isEmpty()) {
			return true;
		}
		//
		return false;
	}

	private String getMoleculeInformation(ILibraryInformation libraryInformation) {

		StringBuilder builder = new StringBuilder();
		//
		builder.append(getInfo(libraryInformation.getName()));
		builder.append(" | ");
		builder.append(getInfo(libraryInformation.getCasNumber()));
		builder.append(" | ");
		builder.append(getInfo(libraryInformation.getSmiles()));
		//
		return builder.toString();
	}

	private String getInfo(String value) {

		return value.isEmpty() ? "--" : value;
	}

	private IMoleculeImageService getMoleculeImageService() {

		Object object = comboViewerServices.getStructuredSelection().getFirstElement();
		if(object instanceof IMoleculeImageService) {
			return (IMoleculeImageService)object;
		}
		//
		return null;
	}

	private ImageServiceInput getImageInput() {

		Object object = comboViewerInput.getStructuredSelection().getFirstElement();
		if(object instanceof ImageServiceInput) {
			return (ImageServiceInput)object;
		}
		/*
		 * Default
		 */
		return ImageServiceInput.NAME;
	}

	private String getExportName() {

		String input = textInput.getText().trim();
		String name = input.replaceAll(":", "");
		name = name.isEmpty() ? "Unkown" : name;
		int length = preferenceStore.getInt(PreferenceConstants.P_LENGTH_MOLECULE_NAME_EXPORT);
		if(length >= PreferenceConstants.MIN_LENGTH_NAME_EXPORT && name.length() > length) {
			return name.substring(0, 20);
		} else {
			return name;
		}
	}

	private void updateContent(Display display) {

		updateWidgets();
		canvasMolecule.redraw();
	}

	private void updateWidgets() {

		setTextMolecule();
		setTextInput();
	}

	private void setTextMolecule() {

		if(libraryInformation != null) {
			textMolecule.setText(libraryInformation.getMoleculeStructure());
		} else {
			textMolecule.setText("");
		}
	}

	private void setTextInput() {

		if(libraryInformation != null) {
			ImageServiceInput imageInput = getImageInput();
			switch(imageInput) {
				case SMILES:
					String smiles = libraryInformation.getSmiles();
					if(!smiles.isEmpty()) {
						textInput.setText(smiles);
					}
					break;
				default:
					String name = libraryInformation.getName();
					if(!name.isEmpty()) {
						textInput.setText(name);
					}
					break;
			}
		} else {
			textInput.setText("");
		}
	}

	private static ILibraryInformation createLibraryInformationDefault() {

		ILibraryInformation libraryInformation = new LibraryInformation();
		//
		libraryInformation.setName(THIAMINE_NAME);
		libraryInformation.setCasNumber(THIAMINE_CAS);
		libraryInformation.setSmiles(THIAMINE_SMILES);
		//
		return libraryInformation;
	}

	private ILibraryInformation createLibraryInformationByInput() {

		ILibraryInformation libraryInformation = new LibraryInformation();
		//
		ImageServiceInput imageInput = getImageInput();
		String text = textInput.getText().trim();
		switch(imageInput) {
			case SMILES:
				libraryInformation.setSmiles(text);
				break;
			default:
				libraryInformation.setName(text);
				break;
		}
		//
		return libraryInformation;
	}
}
