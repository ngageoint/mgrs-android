package mil.nga.mgrs.gzd;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
                }
            }
        }

        return bytes;
    }

}
