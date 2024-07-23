/*******************************************************************************
 * Copyright (c) 2020, 2024 Lablicate GmbH.
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

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.chemclipse.swt.ui.services.IMoleculeImageService;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.MoleculeImageServiceSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtchart.extensions.clipboard.ImageArrayTransfer;
import org.eclipse.swtchart.extensions.core.IKeyboardSupport;

public class MoleculeUI extends Composite implements IExtendedPartUI {

	private static final double SCALE_DEFAULT = 1.0d;
	private static final double SCALE_DELTA = 0.1d;
	//
	private static final String EMPTY_MESSAGE = "Please select a target to view a molecular structure.";
	private static final String ERROR_MESSAGE = "The molecule image couldn't be created.";
	//
	private AtomicReference<Canvas> canvasMolecule = new AtomicReference<>();
	//
	private double scaleFactor = SCALE_DEFAULT;
	private Image imageMolecule = null;
	private Point renderedSize;
	private ILibraryInformation renderedLibraryInformation;
	//
	private IMoleculeImageService moleculeImageService = null;
	private ILibraryInformation libraryInformation;

	public MoleculeUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Override
	public void dispose() {

		if(imageMolecule != null) {
			imageMolecule.dispose();
		}
	}

	public void setMoleculeImageService(IMoleculeImageService moleculeImageService) {

		this.moleculeImageService = moleculeImageService;
	}

	public void setInput(Display display, ILibraryInformation libraryInformation) {

		this.libraryInformation = libraryInformation;
		createMoleculeImage(display);
		canvasMolecule.get().redraw();
	}

	public void clear(Display display) {

		scaleFactor = SCALE_DEFAULT;
		libraryInformation = null;
		renderedLibraryInformation = null;
		createMoleculeImage(display);
	}

	/**
	 * May return null..
	 * 
	 * @return {@link ImageData}
	 */
	public ImageData getImageData() {

		return imageMolecule != null ? imageMolecule.getImageData() : null;
	}

	private void createControl() {

		setLayout(new FillLayout());
		canvasMolecule.set(createCanvasMolecule(this));
		initialize();
	}

	private void initialize() {

		moleculeImageService = MoleculeImageServiceSupport.getMoleculeImageServiceSelection();
	}

	private Canvas createCanvasMolecule(Composite parent) {

		Canvas canvas = new Canvas(parent, SWT.FILL);
		canvas.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
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

				if(moleculeImageService != null && moleculeImageService.isOnline()) {
					return;
				} else {
					scaleFactor += (event.count > 0) ? SCALE_DELTA : -SCALE_DELTA;
					canvas.redraw();
				}
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
		canvas.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.stateMask == SWT.MOD1 && e.keyCode == IKeyboardSupport.KEY_CODE_LC_C) {
					ImageData imageData = imageMolecule.getImageData();
					if(imageData != null) {
						Clipboard clipboard = new Clipboard(e.display);
						try {
							if(OperatingSystemUtils.isWindows()) {
								clipboard.setContents(new Object[]{imageData, imageData}, new Transfer[]{ImageArrayTransfer.getImageTransferWindows(), ImageArrayTransfer.getInstanceWindows()});
							} else if(OperatingSystemUtils.isLinux()) {
								clipboard.setContents(new Object[]{imageData}, new Transfer[]{ImageArrayTransfer.getInstanceLinux()});
							} else if(OperatingSystemUtils.isMac() || OperatingSystemUtils.isUnix()) {
								clipboard.setContents(new Object[]{imageData}, new Transfer[]{ImageArrayTransfer.getImageTransferMacOS()});
							}
						} finally {
							if(!clipboard.isDisposed()) {
								clipboard.dispose();
							}
						}
					}
				}
			}
		});
		//
		return canvas;
	}

	private void drawImage(Canvas canvas, PaintEvent paintEvent) {

		drawImage(canvas, paintEvent.display, paintEvent.gc);
	}

	private void drawImage(Canvas canvas, Display display, GC gc) {

		if(libraryInformation == null || !isSourceDataAvailable(libraryInformation)) {
			/*
			 * Instructions
			 */
			Font font = getFont();
			FontData[] fontData = font.getFontData();
			int width = gc.stringExtent(EMPTY_MESSAGE).x;
			int height = fontData[0].getHeight();
			//
			Point size = canvas.getSize();
			int x = (int)(size.x / 2.0d - width / 2.0d);
			int y = (int)(size.y / 2.0d - height / 2.0d);
			gc.drawText(EMPTY_MESSAGE, x, y, true);
		} else {
			/*
			 * Molecule
			 */
			createMoleculeImage(display);
			if(imageMolecule != null) {
				/*
				 * Image of molecule.
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
					Point size = canvasMolecule.get().getSize();
					int corrwidth = (int)(size.x * scaleFactor);
					int corrheight = (int)(size.y * scaleFactor);
					destX = (int)(srcWidth / 2.0d - destWidth / 2.0d - corrwidth / 2.0d);
					destY = (int)(srcHeight / 2.0d - destHeight / 2.0d - corrheight / 2.0d);
				}
				/*
				 * Correction
				 */
				Point destSize = adjustSize(destWidth, destHeight);
				gc.drawImage(imageMolecule, srcX, srcY, srcWidth, srcHeight, destX, destY, destSize.x, destSize.y);
			} else {
				/*
				 * Can't create molecule display.
				 */
				Font font = getFont();
				FontData[] fontData = font.getFontData();
				int width = gc.stringExtent(ERROR_MESSAGE).x;
				int height = fontData[0].getHeight();
				//
				Point size = canvas.getSize();
				int x = (int)(size.x / 2.0d - width / 2.0d);
				int y = (int)(size.y / 2.0d - height / 2.0d);
				gc.drawText(ERROR_MESSAGE, x, y, true);
			}
		}
	}

	private boolean isSourceDataAvailable(ILibraryInformation libraryInformation) {

		if(libraryInformation != null) {
			if(!libraryInformation.getName().isEmpty()) {
				return true;
			} else if(!libraryInformation.getCasNumber().isEmpty()) {
				return true;
			} else if(!libraryInformation.getSmiles().isEmpty()) {
				return true;
			} else if(!libraryInformation.getInChI().isEmpty()) {
				return true;
			}
		}
		//
		return false;
	}

	private void createMoleculeImage(Display display) {

		if(moleculeImageService != null) {
			/*
			 * Canvas and a scale factor are used.
			 */
			Point size = calculateImageSize();
			int width = size.x;
			int height = size.y;
			/*
			 * Skip creation if the molecule image exists already.
			 * Take a size change of the canvas into account.
			 */
			if(!size.equals(renderedSize) || renderedLibraryInformation != libraryInformation) {
				if(isSourceDataAvailable(libraryInformation)) {
					/*
					 * Dispose is required on images.
					 */
					if(imageMolecule != null) {
						imageMolecule.dispose();
						imageMolecule = null;
					}
					/*
					 * Create a new molecule image.
					 */
					imageMolecule = moleculeImageService.create(display, libraryInformation, width, height);
					renderedLibraryInformation = libraryInformation;
					renderedSize = size;
				} else {
					logger.info(ERROR_MESSAGE);
				}
			}
		}
	}

	private Point calculateImageSize() {

		Point size = canvasMolecule.get().getSize();
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
}