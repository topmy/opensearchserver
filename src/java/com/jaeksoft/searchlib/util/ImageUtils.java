/**   
 * License Agreement for OpenSearchServer
 *
 * Copyright (C) 2012-2013 Emmanuel Keller / Jaeksoft
 * 
 * http://www.open-search-server.com
 * 
 * This file is part of OpenSearchServer.
 *
 * OpenSearchServer is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * OpenSearchServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenSearchServer. 
 *  If not, see <http://www.gnu.org/licenses/>.
 **/

package com.jaeksoft.searchlib.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.util.Collection;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.swing.ImageIcon;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Rotation;

import com.jaeksoft.searchlib.Logging;

public class ImageUtils {

	public static BufferedImage toBufferedImage(Image image)
			throws InterruptedException {

		if (image instanceof BufferedImage)
			return (BufferedImage) image;

		image = new ImageIcon(image).getImage();
		int type = hasAlpha(image) ? BufferedImage.TYPE_INT_ARGB
				: BufferedImage.TYPE_INT_RGB;
		BufferedImage bimage = new BufferedImage(image.getWidth(null),
				image.getHeight(null), type);
		Graphics g = bimage.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return bimage;
	}

	public static BufferedImage rotate(BufferedImage image, float degree)
			throws InterruptedException {

		Rotation rot = null;
		AffineTransformOp[] xform = null;

		switch ((int) degree) {
		case 90:
			rot = Rotation.CW_90;
			break;
		case 180:
			rot = Rotation.CW_180;
			break;
		case 270:
			rot = Rotation.CW_270;
			break;
		default:
			xform = new AffineTransformOp[1];
			xform[0] = new AffineTransformOp(
					AffineTransform.getRotateInstance(Math.toRadians(degree)),
					AffineTransformOp.TYPE_BICUBIC);
			break;
		}
		if (rot != null)
			return Scalr.rotate(image, rot, xform);
		return Scalr.apply(image, xform);
	}

	public static boolean hasAlpha(Image image) throws InterruptedException {
		if (image instanceof BufferedImage)
			return ((BufferedImage) image).getColorModel().hasAlpha();
		PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
		pg.grabPixels();
		return pg.getColorModel().hasAlpha();
	}

	public static void checkPlugins() {
		ImageIO.scanForPlugins();
		if (Logging.isDebug)
			for (String suffix : ImageIO.getReaderFileSuffixes())
				Logging.debug("ImageIO suffix: " + suffix);
	}

	public static ImageReader findImageReader(String formatName) {
		Iterator<ImageReader> readers = ImageIO
				.getImageReadersByFormatName(formatName);
		ImageReader reader = null;
		while (readers.hasNext()) {
			reader = (ImageReader) readers.next();
			if (reader.canReadRaster())
				return reader;
		}
		return null;
	}

	public static final boolean checkIfManyColors(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		if (w == 0 && h == 0)
			return false;
		int unicolor = image.getRGB(0, 0);
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int pixel = image.getRGB(x, y);
				if (pixel != unicolor)
					return true;
			}
		}
		return false;
	}

	public static final void yellowHighlight(Image image,
			Collection<Rectangle> boxes) {
		if (boxes == null)
			return;
		if (boxes.size() == 0)
			return;
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.setColor(Color.YELLOW);
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				0.4f);
		g2d.setComposite(ac);
		for (Rectangle box : boxes)
			g2d.fill(box);
	}

	public static final String rectToCoordString(Rectangle box, char separator) {
		StringBuffer sb = new StringBuffer();
		sb.append(Integer.toString(box.x));
		sb.append(separator);
		sb.append(Integer.toString(box.y));
		sb.append(separator);
		sb.append(Integer.toString(box.x + box.width));
		sb.append(separator);
		sb.append(Integer.toString(box.y + box.height));
		return sb.toString();
	}
}
