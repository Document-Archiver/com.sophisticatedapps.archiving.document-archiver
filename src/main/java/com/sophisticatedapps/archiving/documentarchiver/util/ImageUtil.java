package com.sophisticatedapps.archiving.documentarchiver.util;

import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoShort;

import java.io.File;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants.*;

public class ImageUtil {

    public static OrientationInformation getOrientationInformation(File anImageFile) {

        return OrientationInformation.byOrientationId(getExifOrientationId(anImageFile));
    }

    /**
     * Retrieve the orientation of a file from the EXIF data.
     *
     * @param   anImageFile   The image file.
     * @return  The orientation value ID.
     */
    private static int getExifOrientationId(final File anImageFile) {

        try {

            final ImageMetadata tmpMetadata = Imaging.getMetadata(anImageFile);
            TiffImageMetadata tmpTiffImageMetadata;

            if (tmpMetadata instanceof JpegImageMetadata) {

                tmpTiffImageMetadata = ((JpegImageMetadata) tmpMetadata).getExif();
            }
            else if (tmpMetadata instanceof TiffImageMetadata) {

                tmpTiffImageMetadata = (TiffImageMetadata) tmpMetadata;
            }
            else {

                return TiffTagConstants.ORIENTATION_VALUE_HORIZONTAL_NORMAL;
            }

            TiffField tmpTiffField = tmpTiffImageMetadata.findField(TiffTagConstants.TIFF_TAG_ORIENTATION);

            if (!Objects.isNull(tmpTiffField)) {

                return tmpTiffField.getIntValue();
            }
            else {

                // https://www.loc.gov/preservation/digital/formats/content/tiff_tags.shtml
                TagInfo tmpTagInfo = new TagInfoShort("Orientation", 274, TiffDirectoryType.TIFF_DIRECTORY_IFD0);
                tmpTiffField = tmpTiffImageMetadata.findField(tmpTagInfo);

                if (!Objects.isNull(tmpTiffField)) {

                    return tmpTiffField.getIntValue();
                }
                else {

                    return TiffTagConstants.ORIENTATION_VALUE_HORIZONTAL_NORMAL;
                }
            }
        }
        catch (Exception e) {

            return TiffTagConstants.ORIENTATION_VALUE_HORIZONTAL_NORMAL;
        }
    }

    /**
     * Enum for the different orientation values.
     */
    public enum OrientationInformation {

        HORIZONTAL_NORMAL(0, false, false, ORIENTATION_VALUE_HORIZONTAL_NORMAL),
        MIRROR_HORIZONTAL(0, true, false, ORIENTATION_VALUE_MIRROR_HORIZONTAL),
        ROTATE_180(180, false, false, ORIENTATION_VALUE_ROTATE_180),
        MIRROR_VERTICAL(0, false, true, ORIENTATION_VALUE_MIRROR_VERTICAL),
        MIRROR_HORIZONTAL_AND_ROTATE_270_CW(270, true, false, ORIENTATION_VALUE_MIRROR_HORIZONTAL_AND_ROTATE_270_CW),
        ROTATE_90_CW(90, false, false, ORIENTATION_VALUE_ROTATE_90_CW),
        MIRROR_HORIZONTAL_AND_ROTATE_90_CW(90, true, false, ORIENTATION_VALUE_MIRROR_HORIZONTAL_AND_ROTATE_90_CW),
        ROTATE_270_CW(270, false, false, ORIENTATION_VALUE_ROTATE_270_CW);

        private static final Map<Integer, OrientationInformation> LOOKUP = new HashMap<>();

        private final double rotationCw;
        private final boolean mirroredHorizontal;
        private final boolean mirroredVertical;
        private final int orientationId;

        static {

            for (OrientationInformation tmpCurrentType : EnumSet.allOf(OrientationInformation.class)) {

                LOOKUP.put(tmpCurrentType.orientationId, tmpCurrentType);
            }
        }

        OrientationInformation(double aRotationCw, boolean anIsMirroredHorizontal, boolean anIsMirroredVertical, int anOrientationId) {

            this.rotationCw = aRotationCw;
            this.mirroredHorizontal = anIsMirroredHorizontal;
            this.mirroredVertical = anIsMirroredVertical;
            this.orientationId = anOrientationId;
        }

        public static OrientationInformation byOrientationId(int anOrientationId) {

            return LOOKUP.get(anOrientationId);
        }

        public double getRotationCw() {

            return rotationCw;
        }

        public boolean isMirroredHorizontal() {

            return mirroredHorizontal;
        }

        public boolean isMirroredVertical() {

            return mirroredVertical;
        }
    }

}
