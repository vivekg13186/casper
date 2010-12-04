package com.casper;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

class Localizer {

    private static ResourceBundle bundle = null;

    static {
        try {
        bundle = ResourceBundle.getBundle(
            "com.casper.compiler.LocalStrings");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /*
     * Returns the localized error message corresponding to the given error
     * code.
     *
     * If the given error code is not defined in the resource bundle for
     * localized error messages, it is used as the error message.
     *
     * @param errCode Error code to localize
     *
     * @return Localized error message
     */
    public static String getMessage(String errCode) {
        String errMsg = errCode;
        try {
            errMsg = bundle.getString(errCode);
        } catch (MissingResourceException e) {
        }
        return errMsg;
    }

    /*
     * Returns the localized error message corresponding to the given error
     * code.
     *
     * If the given error code is not defined in the resource bundle for
     * localized error messages, it is used as the error message.
     *
     * @param errCode Error code to localize
     * @param arg Argument for parametric replacement
     *
     * @return Localized error message
     */
    public static String getMessage(String errCode, String arg) {
        return getMessage(errCode, new Object[] {arg});
    }

    /*
     * Returns the localized error message corresponding to the given error
     * code.
     *
     * If the given error code is not defined in the resource bundle for
     * localized error messages, it is used as the error message.
     *
     * @param errCode Error code to localize
     * @param arg1 First argument for parametric replacement
     * @param arg2 Second argument for parametric replacement
     *
     * @return Localized error message
     */
    public static String getMessage(String errCode, String arg1, String arg2) {
        return getMessage(errCode, new Object[] {arg1, arg2});
    }

    /*
     * Returns the localized error message corresponding to the given error
     * code.
     *
     * If the given error code is not defined in the resource bundle for
     * localized error messages, it is used as the error message.
     *
     * @param errCode Error code to localize
     * @param arg1 First argument for parametric replacement
     * @param arg2 Second argument for parametric replacement
     * @param arg3 Third argument for parametric replacement
     *
     * @return Localized error message
     */
    public static String getMessage(String errCode, String arg1, String arg2,
                                    String arg3) {
        return getMessage(errCode, new Object[] {arg1, arg2, arg3});
    }

    /*
     * Returns the localized error message corresponding to the given error
     * code.
     *
     * If the given error code is not defined in the resource bundle for
     * localized error messages, it is used as the error message.
     *
     * @param errCode Error code to localize
     * @param arg1 First argument for parametric replacement
     * @param arg2 Second argument for parametric replacement
     * @param arg3 Third argument for parametric replacement
     * @param arg4 Fourth argument for parametric replacement
     *
     * @return Localized error message
     */
    public static String getMessage(String errCode, String arg1, String arg2,
                                    String arg3, String arg4) {
        return getMessage(errCode, new Object[] {arg1, arg2, arg3, arg4});
    }

    /*
     * Returns the localized error message corresponding to the given error
     * code.
     *
     * If the given error code is not defined in the resource bundle for
     * localized error messages, it is used as the error message.
     *
     * @param errCode Error code to localize
     * @param args Arguments for parametric replacement
     *
     * @return Localized error message
     */
    public static String getMessage(String errCode, Object[] args) {
        String errMsg = errCode;
        try {
            errMsg = bundle.getString(errCode);
            if (args != null) {
                MessageFormat formatter = new MessageFormat(errMsg);
                errMsg = formatter.format(args);
            }
        } catch (MissingResourceException e) {
        }

        return errMsg;
    }
}
