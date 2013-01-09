/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;

/**
 * Base64 Converter.
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class Base64 {

    //~ Static fields/initializers ---------------------------------------------

    //J-
    private static final byte[] BASE64CODE = new byte[] {
        // A-Z
        0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4A, 0x4B, 0x4C, 0x4D, 0x4E, 0x4F,
        0x50, 0x51, 0x52, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5A,
        // a-z
        0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6A, 0x6B, 0x6C, 0x6D, 0x6E, 0x6F,
        0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7A,
        // 0-9
        0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39,
        // + /
        0x2B, 0x2F
    };
    //J+

    private static final byte LF = 0xA;
    private static final byte CR = 0xD;
    private static final byte EQ = 0x3D;

    private static final int LINE_SPLIT = 76;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Base64 object.
     */
    private Base64() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Converts the given bytes to base64 and wipes the input array, if desired. All generated temporary data is wiped
     * as soon as it is not needed anymore.
     *
     * @param   byteString  the bytes to be converted
     * @param   wipeInput   if true, the input bytes will be wiped (for security reasons)
     *
     * @return  the base64 encoded input array
     *
     * @throws  IllegalArgumentException  if the given array has length <code>null</code>
     */
    public static byte[] toBase64(final byte[] byteString, final boolean wipeInput) {
        if (byteString == null) {
            throw new IllegalArgumentException("given byte array must not be null"); // NOI18N
        }

        final int padding = (3 - (byteString.length % 3)) % 3;

        final byte[] byteStringPadded = new byte[byteString.length + padding];
        for (int i = 0; i < byteString.length; ++i) {
            byteStringPadded[i] = byteString[i];
            if (wipeInput) {
                byteString[i] = PasswordEncrypter.getWipe();
            }
        }

        final byte[] base64 = new byte[(byteStringPadded.length / 3) * 4];
        int k = 0;
        for (int i = 0; i < byteStringPadded.length; i += 3) {
            final int j = ((byteStringPadded[i] & 0xFF) << 16)
                        + ((byteStringPadded[i + 1] & 0xFF) << 8)
                        + (byteStringPadded[i + 2] & 0xFF);

            byteStringPadded[i] = PasswordEncrypter.getWipe();
            byteStringPadded[i + 1] = PasswordEncrypter.getWipe();
            byteStringPadded[i + 2] = PasswordEncrypter.getWipe();

            base64[k++] = BASE64CODE[(j >> 18) & 0x3F];
            base64[k++] = BASE64CODE[(j >> 12) & 0x3F];
            base64[k++] = BASE64CODE[(j >> 6) & 0x3F];
            base64[k++] = BASE64CODE[j & 0x3F];
        }

        // integer div to get line count of additional lines
        final int lineCount = base64.length / LINE_SPLIT;
        final byte[] base64Br = new byte[base64.length + (lineCount * 2)];
        for (int i = 0, j = 0; i < base64.length; ++i, ++j) {
            if ((i > 0) && ((i % LINE_SPLIT) == 0)) {
                base64Br[j++] = CR;
                base64Br[j++] = LF;
            }
            base64Br[j] = base64[i];
            base64[i] = PasswordEncrypter.getWipe();
        }
        for (int i = base64Br.length - 1; i > (base64Br.length - 1 - padding); --i) {
            base64Br[i] = EQ;
        }

        return base64Br;
    }

    /**
     * Converts the given bytes back from base64 and wipes the input array, if desired. All generated temporary data is
     * wiped as soon as it is not needed anymore.
     *
     * @param   byteString  the bytes to be converted back
     * @param   wipeInput   if true, the input bytes will be wiped (for security reasons)
     *
     * @return  the decoded input array
     *
     * @throws  IllegalArgumentException  if the given array is of length <code>null</code> or not multiple of four
     *                                    after the CR LR are stripped or if any of the bytes within the given array are
     *                                    not property encoded
     */
    public static byte[] fromBase64(final byte[] byteString, final boolean wipeInput) {
        if (byteString == null) {
            throw new IllegalArgumentException("given byte array must not be null"); // NOI18N
        }
        // at least four bytes present in encoded string
        if (byteString.length == 0) {
            throw new IllegalArgumentException("incorrectly encoded string, too few bytes: " + byteString.length);
        }

        final int padding;
        if (byteString[byteString.length - 2] == EQ) {
            padding = 2;
        } else if (byteString[byteString.length - 1] == EQ) {
            padding = 1;
        } else {
            padding = 0;
        }

        // integer div to get the additional line count (the number of CR LF)
        final int lineCount = byteString.length / (LINE_SPLIT + 2);

        final byte[] strippedBr = new byte[byteString.length - (lineCount * 2)];
        for (int i = 0, j = 0; i < byteString.length; ++i, ++j) {
            if ((j > 0) && ((j % LINE_SPLIT) == 0)) {
                if (wipeInput) {
                    byteString[i++] = PasswordEncrypter.getWipe();
                    byteString[i++] = PasswordEncrypter.getWipe();
                } else {
                    i += 2;
                }
            }

            strippedBr[j] = byteString[i];

            if (wipeInput) {
                byteString[i] = PasswordEncrypter.getWipe();
            }
        }

        if ((strippedBr.length % 4) != 0) {
            throw new IllegalArgumentException(
                "incorrectly encoded string, must be multible of four when CR LF is stripped: " // NOI18N
                        + strippedBr.length);
        }

        final byte[] decoded = new byte[((strippedBr.length / 4) * 3) - padding];
        int k = 0;
        for (int i = 0; i < strippedBr.length; i += 4) {
            final int j = ((indexOf(strippedBr[i]) & 0x3F) << 18)
                        + ((indexOf(strippedBr[i + 1]) & 0x3F) << 12)
                        + ((indexOf(strippedBr[i + 2]) & 0x3F) << 6)
                        + (indexOf(strippedBr[i + 3]) & 0x3F);

            strippedBr[i] = PasswordEncrypter.getWipe();
            strippedBr[i + 1] = PasswordEncrypter.getWipe();
            strippedBr[i + 2] = PasswordEncrypter.getWipe();
            strippedBr[i + 3] = PasswordEncrypter.getWipe();

            decoded[k++] = (byte)((j >> 16) & 0xFF);
            if ((i < (strippedBr.length - 4)) || (padding < 2)) {
                decoded[k++] = (byte)((j >> 8) & 0xFF);
            }
            if ((i < (strippedBr.length - 4)) || (padding < 1)) {
                decoded[k++] = (byte)(j & 0xFF);
            }
        }

        return decoded;
    }

    /**
     * Used to convert base64 char to corresponding index/bytes.
     *
     * @param   b64  the base64 char integer
     *
     * @return  the base64 bytes/index corresponding to the base64 char integer, or 0 for padding char '<code>=</code>'
     *
     * @throws  IllegalArgumentException  if the given integer is not a valid base64 char
     *
     * @see     #BASE64CODE
     */
    private static int indexOf(final int b64) {
        if ((b64 < 0x2B) || (b64 > 0x7A)) {
            throw new IllegalArgumentException("byte64 not within valid range: " + b64); // NOI18N
        }

        // special padding handling
        if (b64 == EQ) {
            return 0;
        }

        for (int i = 0; i < BASE64CODE.length; ++i) {
            if (BASE64CODE[i] == b64) {
                return i;
            }
        }

        throw new IllegalArgumentException("illegal byte64: " + b64); // NOI18N
    }
}
