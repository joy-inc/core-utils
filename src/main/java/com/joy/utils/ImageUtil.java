package com.joy.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by Daisw on 2016/11/21.
 */

public class ImageUtil {

    private static final String TAG = "ImageUtil";

    public interface Constants {
        String SCHEME_FILE = "file";
        String SCHEME_HTTP = "http";
        String SCHEME_HTTPS = "https";
        String SCHEME_ASSETS = "assets";
        String SCHEME_RESOURCE = "resource";
        String SCHEME_RESOURCE_FULL = "resource://";
        String SCHEME_THUMBNAILS = "thumbnails";
        String SCHEME_THUMBNAILS_FULL = "thumbnails://";
    }

    /**
     * 根据最长边压缩图片（压缩出来的图片最长边一定是>=maxSideLength）
     * add by Daisw
     *
     * @param srcPath       图片的路径
     * @param maxSideLength 长轴的长度，单位px
     * @return
     */
    public static Bitmap getSampleSizeImage(String srcPath, int maxSideLength) {
        return TextUtil.isEmpty(srcPath) ? null : getSampleSizeImage(Uri.parse(srcPath), maxSideLength);
    }

    public static Bitmap getSampleSizeImage(Uri uri, int maxSideLength) {
        if (uri == null) {
            return null;
        }
        try {
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(openInputStream(uri), null, newOpts);
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            if (LogMgr.DEBUG) {
                LogMgr.d(TAG, "getImage original ## w: " + w + " # h: " + h + " # maxSize: " + maxSideLength);
            }
            if (w <= maxSideLength && h <= maxSideLength) {
                newOpts.inJustDecodeBounds = false;
                return BitmapFactory.decodeStream(openInputStream(uri), null, newOpts);
            }
            int minSideLength = -1;
            if (w > h && w > maxSideLength) {// 横图
                w = maxSideLength;
                h = h * w / maxSideLength;
                minSideLength = h;
            } else if (w < h && h > maxSideLength) {// 竖图
                h = maxSideLength;
                w = w * h / maxSideLength;
                minSideLength = w;
            } else if (w == h && w > maxSideLength) {// 方图
                w = maxSideLength;
                h = maxSideLength;
                minSideLength = w;
            }
            int rate = computeSampleSize(newOpts, minSideLength, w * h);
            if (LogMgr.DEBUG) {
                LogMgr.d(TAG, "getImage compress ## rate: " + rate);
            }
            newOpts.inSampleSize = rate;
            newOpts.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeStream(openInputStream(uri), null, newOpts);
            if (LogMgr.DEBUG) {
                LogMgr.d(TAG, "getImage changed ## width: " + bitmap.getWidth() + " # height: " + bitmap.getHeight());
            }
            return bitmap;
        } catch (Throwable t) {
            if (LogMgr.DEBUG) {
                t.printStackTrace();
                System.gc();
            }
        }
        return null;
    }

    public static InputStream openInputStream(Uri uri) {
        if (uri == null) {
            return null;
        }
        String scheme = uri.getScheme();
        InputStream stream = null;
        if (scheme == null || ContentResolver.SCHEME_FILE.equals(scheme)) {
            stream = openFileInputStream(uri.getPath());// from file
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            stream = openContentInputStream(uri);// from content
        } else if (Constants.SCHEME_HTTP.equals(scheme) || Constants.SCHEME_HTTPS.equals(scheme)) {
            stream = openRemoteInputStream(uri);// from remote url
        } else if (Constants.SCHEME_RESOURCE.equals(scheme)) {
            stream = openResourceStream(uri);// from resource
        } else if (Constants.SCHEME_ASSETS.equals(scheme)) {
            stream = openAssetsStream(uri);// from assets
        }
        return stream;
    }

    private static InputStream openFileInputStream(String path) {
        try {
            return new FileInputStream(path);
        } catch (Exception e) {
            if (LogMgr.DEBUG) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static InputStream openContentInputStream(Uri uri) {
//        try {
//            return ExApplication.getContext().getContentResolver().openInputStream(uri);
//        } catch (Exception e) {
//            if (LogMgr.DEBUG) {
//                e.printStackTrace();
//            }
//            return null;
//        }
        return null;// TODO: 2016/11/22 待补充 Daisw
    }

    private static InputStream openRemoteInputStream(Uri uri) {
        return null;// TODO: 2016/11/22 待补充 Daisw
    }

    public static InputStream openResourceStream(Uri uri) {
//        try {
//            return ExApplication.getContext().getResources().openRawResource(Integer.parseInt(uri.getHost()));
//        } catch (Exception e) {
//            if (LogMgr.DEBUG) {
//                e.printStackTrace();
//            }
//            return null;
//        }
        return null;// TODO: 2016/11/22 待补充 Daisw
    }

    public static InputStream openAssetsStream(Uri uri) {
//        try {
//            return ExApplication.getContext().getAssets().open(uri.getPath().substring(1));
//        } catch (Exception e) {
//            if (LogMgr.DEBUG) {
//                e.printStackTrace();
//            }
//            return null;
//        }
        return null;// TODO: 2016/11/22 待补充 Daisw
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;// return the larger one when there is no overlapping zone.
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 缩放图片，保持图片的宽高比，最长边 = maxSideLength
     * add by Daisw
     *
     * @param bitmap        源
     * @param maxSideLength 长轴的长度，单位px
     * @param filePath      图片的路径，用来检验图片是否被旋转
     * @return
     */
    public static Bitmap getScaleImageByDegree(Bitmap bitmap, int maxSideLength, String filePath) {
        if (bitmap == null) {
            return null;
        }
        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (LogMgr.DEBUG) {
                LogMgr.d(TAG, "getImage original ## width: " + width + " # height: " + height);
            }
            Matrix matrix = new Matrix();
            if (width > height) {
                float rate = (float) maxSideLength / width;
                matrix.postScale(rate, rate);
            } else {
                float rate = (float) maxSideLength / height;
                matrix.postScale(rate, rate);
            }
            if (filePath != null) {
                matrix.postRotate(getImageDegree(filePath));
            }
            Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            if (LogMgr.DEBUG) {
                LogMgr.d(TAG, "getImage changed ## width: " + newBitmap.getWidth() + " # height: " + newBitmap.getHeight());
            }
            return newBitmap;
        } catch (Throwable t) {
            if (LogMgr.DEBUG) {
                t.printStackTrace();
                System.gc();
            }
        }
        return null;
    }

    /**
     * 读取图片属性：旋转的角度
     * add by Daisw
     *
     * @param srcPath 图片绝对路径
     * @return degree 旋转的角度
     */
    public static int getImageDegree(String srcPath) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(srcPath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    break;
            }
        } catch (Throwable t) {
            if (LogMgr.DEBUG) {
                t.printStackTrace();
            }
        }
        return degree;
    }

    /**
     * 根据最大边长，重置指定byte[]数据形式的Bitmap宽高
     * add by Daisw
     *
     * @param binary  Bitmap的byte[]形式数据
     * @param maxSize 最大边长度
     * @return int[] 处理后的宽、高值
     */
    public static int[] getMaxNumOfPixels(byte[] binary, int maxSize) {
        return getMaxNumOfPixels(byteArrayToBitmap(binary), maxSize);
    }

    /**
     * 根据最大边长，重置指定图片宽高
     * add by Daisw
     *
     * @param bitmap  图片
     * @param maxSize 最大边长度
     * @return int[] 处理后的宽、高值
     */
    public static int[] getMaxNumOfPixels(Bitmap bitmap, int maxSize) {
        if (bitmap == null) {
            return new int[]{0, 0};
        }
        return resize(bitmap.getWidth(), bitmap.getHeight(), maxSize);
    }

    /**
     * add by Daisw
     *
     * @param data
     * @return Bitmap
     */
    private static Bitmap byteArrayToBitmap(byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (Throwable t) {
            if (LogMgr.DEBUG) {
                t.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 压缩的图片，以jpeg方式压缩<br>
     * 需要注意的是如果压缩的背景有透明色就不能使用jpeg方法
     * add by Daisw
     *
     * @param image
     * @param quality Hint to the compressor, 0-100. 0 meaning compress for
     *                small size, 100 meaning compress for max quality. Some
     *                formats, like PNG which is lossless, will ignore the
     *                quality setting
     * @return
     */
    public static byte[] bitmapToByteArray(Bitmap image, int quality) {
        if (image == null) {
            return null;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            return baos.toByteArray();
        } catch (Throwable t) {
            if (LogMgr.DEBUG) {
                t.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 根据指定最大边长重置宽、高值
     * add by Daisw
     *
     * @param w       宽
     * @param h       高
     * @param maxSize 最大边长度
     * @return int[] 处理后的宽、高值
     */
    private static int[] resize(int w, int h, int maxSize) {
        if (LogMgr.DEBUG) {
            LogMgr.d(TAG, "getMaxNumOfPixels original ## w: " + w + " # h: " + h);
        }
        if (w > h) {// 横图
            h = (int) (h / ((float) w / maxSize));
            w = maxSize;
        } else if (w < h) {// 竖图
            w = (int) (w / ((float) h / maxSize));
            h = maxSize;
        } else if (w == h) {// 方图
            w = maxSize;
            h = maxSize;
        }
        if (LogMgr.DEBUG) {
            LogMgr.d(TAG, "getMaxNumOfPixels changed ## w: " + w + " # h: " + h);
        }
        return new int[]{w, h};
    }

    /**
     * 根据指定方向旋转图片
     * add by Daisw
     *
     * @param degree 目标方向，0为不旋转
     * @param bitmap 要旋转的图片
     * @return Bitmap 处理后的Bitmap
     */
    public static Bitmap rotateImageView(int degree, Bitmap bitmap) {
        if (degree == 0 || bitmap == null) {
            return bitmap;
        }
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return newBitmap;
        } catch (Throwable t) {
            return null;
        }
    }
}
