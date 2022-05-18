package mil.nga.mgrs.tile;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Tile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import mil.nga.mgrs.features.Point;

/**
 * Tile Utils
 *
 * @author wnewman
 * @author osbornb
 */
public class TileUtils {

    /**
     * Compress the bitmap to a byte array
     *
     * @param bitmap bitmap
     * @return bytes
     */
    public static byte[] toBytes(Bitmap bitmap) {

        byte[] bytes = null;

        if (bitmap != null) {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
                bytes = byteStream.toByteArray();
            } finally {
                try {
                    byteStream.close();
                } catch (IOException e) {
                    Log.w(TileUtils.class.getSimpleName(),
                            "Failed to close bitmap compression byte stream", e);
                }
            }
        }

        return bytes;
    }

    /**
     * Compress the bitmap to a tile
     *
     * @param bitmap bitmap
     * @return tile
     */
    public static Tile toTile(Bitmap bitmap) {

        Tile tile = null;

        byte[] bytes = toBytes(bitmap);

        if (bytes != null) {
            tile = new Tile(bitmap.getWidth(), bitmap.getHeight(), bytes);
        }

        return tile;
    }

    /**
     * Convert a map coordinate to a point
     *
     * @param latLng map coordinate
     * @return point
     */
    public static Point toPoint(LatLng latLng) {
        return Point.degrees(latLng.longitude, latLng.latitude);
    }

}
