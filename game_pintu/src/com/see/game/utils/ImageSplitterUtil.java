package com.see.game.utils;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;

public class ImageSplitterUtil {
	/**
	 * 
	 * @param bitmap
	 *            传入bitmap
	 * @param piece
	 *            切成piece*piece块
	 * @return List<ImagePiece>
	 */
	public static List<ImagePiece> splitImage(Bitmap bitmap, int piece) {

		List<ImagePiece> imagePieces = new ArrayList<ImagePiece>();

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		int pieceWidth = Math.min(width, height) / piece;
		for (int i = 0; i < piece; i++) {
			for (int j = 0; j < piece; j++) {
				ImagePiece imagePiece = new ImagePiece();
				imagePiece.setIndex(j + i * piece);

				int x = j * pieceWidth;
				int y = i * pieceWidth;

				imagePiece.setBitmap(Bitmap.createBitmap(bitmap, x, y,
						pieceWidth, pieceWidth));
				System.out.println("每一次的小bitmap:::"
						+ Bitmap.createBitmap(bitmap, x, y, pieceWidth,
								pieceWidth).toString());
				imagePieces.add(imagePiece);

			}
		}

		return imagePieces;
	}

}
