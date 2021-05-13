package com.sophisticatedapps.archiving.documentarchiver.util;

import com.sophisticatedapps.archiving.documentarchiver.BaseTest;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants.ORIENTATION_VALUE_ROTATE_90_CW;
import static org.junit.jupiter.api.Assertions.*;

class ImageUtilTest extends BaseTest {

    @Test
    void getOrientationInformation() {

        ImageUtil.OrientationInformation tmpOrientationInfo = ImageUtil.getOrientationInformation(TEST_JPG_FILE);
        assertSame(ImageUtil.OrientationInformation.ROTATE_270_CW, tmpOrientationInfo);

        ImageUtil.OrientationInformation tmpOrientationInfo2 = ImageUtil.getOrientationInformation(TEST_JPG_FILE2);
        assertSame(ImageUtil.OrientationInformation.HORIZONTAL_NORMAL, tmpOrientationInfo2);
    }

    @Test
    void testOrientationInformation_byOrientationId() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {

        assertSame(ImageUtil.OrientationInformation.ROTATE_90_CW, MethodUtils.invokeStaticMethod(
                ImageUtil.OrientationInformation.class, "byOrientationId", ORIENTATION_VALUE_ROTATE_90_CW));
    }

    @Test
    void testOrientationInformation_getRotationCw() {

        assertEquals(270, ImageUtil.OrientationInformation.MIRROR_HORIZONTAL_AND_ROTATE_270_CW.getRotationCw());
    }

    @Test
    void testOrientationInformation_isMirroredHorizontal() {

        assertTrue(ImageUtil.OrientationInformation.MIRROR_HORIZONTAL.isMirroredHorizontal());
        assertFalse(ImageUtil.OrientationInformation.ROTATE_180.isMirroredHorizontal());
    }

    @Test
    void testOrientationInformation_isMirroredVertical() {

        assertFalse(ImageUtil.OrientationInformation.MIRROR_HORIZONTAL_AND_ROTATE_90_CW.isMirroredVertical());
        assertTrue(ImageUtil.OrientationInformation.MIRROR_VERTICAL.isMirroredVertical());
    }

}
