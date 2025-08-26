package com.allo.utils;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class IntentUtils {

    private IntentUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Return the intent of launch app.
     *
     * @param pkgName The name of the package.
     * @return the intent of launch app
     */
    public static Intent getLaunchAppIntent(final String pkgName) {

        String launcherActivity = ActivityUtils.Companion.getLauncherActivity(pkgName);
        if (TextUtils.isEmpty(launcherActivity)) return null;
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(pkgName, launcherActivity);
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * Return the intent of launch app details settings.
     *
     * @param pkgName The name of the package.
     * @return the intent of launch app details settings
     */
    public static Intent getLaunchAppDetailsSettingsIntent(final String pkgName) {
        return getLaunchAppDetailsSettingsIntent(pkgName, false);
    }

    /**
     * Return the intent of launch app details settings.
     *
     * @param pkgName The name of the package.
     * @return the intent of launch app details settings
     */
    public static Intent getLaunchAppDetailsSettingsIntent(final String pkgName, final boolean isNewTask) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + pkgName));
        return getIntent(intent, isNewTask);
    }

    /**
     * Return the intent of share text.
     *
     * @param content The content.
     * @return the intent of share text
     */
    public static Intent getShareTextIntent(final String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent = Intent.createChooser(intent, "");
        return getIntent(intent, true);
    }


    /**
     * Return the intent of share image.
     *
     * @param content  The content.
     * @param imageUri The uri of image.
     * @return the intent of share image
     */
    public static Intent getShareTextImageIntent(@Nullable final String content, final Uri imageUri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.setType("image/*");
        intent = Intent.createChooser(intent, "");
        return getIntent(intent, true);
    }


    /**
     * Return the intent of share images.
     *
     * @param uris The uris of image.
     * @return the intent of share image
     */
    public static Intent getShareImageIntent(final ArrayList<Uri> uris) {
        return getShareTextImageIntent("", uris);
    }


    /**
     * Return the intent of share images.
     *
     * @param content The content.
     * @param uris    The uris of image.
     * @return the intent of share image
     */
    public static Intent getShareTextImageIntent(@Nullable final String content, final ArrayList<Uri> uris) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        intent.setType("image/*");
        intent = Intent.createChooser(intent, "");
        return getIntent(intent, true);
    }

    /**
     * Return the intent of component.
     *
     * @param pkgName   The name of the package.
     * @param className The name of class.
     * @return the intent of component
     */
    public static Intent getComponentIntent(final String pkgName, final String className) {
        return getComponentIntent(pkgName, className, null, false);
    }

    /**
     * Return the intent of component.
     *
     * @param pkgName   The name of the package.
     * @param className The name of class.
     * @param isNewTask True to add flag of new task, false otherwise.
     * @return the intent of component
     */
    public static Intent getComponentIntent(final String pkgName,
                                            final String className,
                                            final boolean isNewTask) {
        return getComponentIntent(pkgName, className, null, isNewTask);
    }

    /**
     * Return the intent of component.
     *
     * @param pkgName   The name of the package.
     * @param className The name of class.
     * @param bundle    The Bundle of extras to add to this intent.
     * @return the intent of component
     */
    public static Intent getComponentIntent(final String pkgName,
                                            final String className,
                                            final Bundle bundle) {
        return getComponentIntent(pkgName, className, bundle, false);
    }

    /**
     * Return the intent of component.
     *
     * @param pkgName   The name of the package.
     * @param className The name of class.
     * @param bundle    The Bundle of extras to add to this intent.
     * @param isNewTask True to add flag of new task, false otherwise.
     * @return the intent of component
     */
    public static Intent getComponentIntent(final String pkgName,
                                            final String className,
                                            final Bundle bundle,
                                            final boolean isNewTask) {
        Intent intent = new Intent();
        if (bundle != null) intent.putExtras(bundle);
        ComponentName cn = new ComponentName(pkgName, className);
        intent.setComponent(cn);
        return getIntent(intent, isNewTask);
    }

    /**
     * Return the intent of shutdown.
     * <p>Requires root permission
     * or hold {@code android:sharedUserId="android.uid.system"},
     * {@code <uses-permission android:name="android.permission.SHUTDOWN" />}
     * in manifest.</p>
     *
     * @return the intent of shutdown
     */
    public static Intent getShutdownIntent() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent = new Intent(Intent.ACTION_SHUTDOWN);
        } else {
            intent = new Intent("com.android.internal.intent.action.REQUEST_SHUTDOWN");
        }
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * Return the intent of dial.
     *
     * @param phoneNumber The phone number.
     * @return the intent of dial
     */
    public static Intent getDialIntent(final String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        return getIntent(intent, true);
    }

    /**
     * Return the intent of call.
     * <p>Must hold {@code <uses-permission android:name="android.permission.CALL_PHONE" />}</p>
     *
     * @param phoneNumber The phone number.
     * @return the intent of call
     */
    public static Intent getCallIntent(final String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        return getIntent(intent, false);
    }

    /**
     * Return the intent of send SMS.
     *
     * @param phoneNumber The phone number.
     * @param content     The content of SMS.
     * @return the intent of send SMS
     */
    public static Intent getSendSmsIntent(final String phoneNumber, final String content) {
        Uri uri = Uri.parse("smsto:" + phoneNumber);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", content);
        return getIntent(intent, true);
    }

    /**
     * Return the intent of capture.
     *
     * @param outUri The uri of output.
     * @return the intent of capture
     */
    public static Intent getCaptureIntent(final Uri outUri) {
        return getCaptureIntent(outUri, false);
    }

    /**
     * Return the intent of capture.
     *
     * @param outUri    The uri of output.
     * @param isNewTask True to add flag of new task, false otherwise.
     * @return the intent of capture
     */
    public static Intent getCaptureIntent(final Uri outUri, final boolean isNewTask) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return getIntent(intent, isNewTask);
    }

    private static Intent getIntent(final Intent intent, final boolean isNewTask) {
        return isNewTask ? intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) : intent;
    }

    public static Intent getContactsIntent(final long id, final String lookup) {
        Uri uri = ContactsContract.Contacts.getLookupUri(id, lookup);
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    public static Intent getContactsIntent(final long id, final String lookup, int needFlag, String name) {
        Uri uri = ContactsContract.Contacts.getLookupUri(id, lookup);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.putExtra("has_register_im_flag", needFlag);
        intent.putExtra("has_register_im_name", name);
        return intent;
    }

    public static Intent getCallSetting() {
        Intent intent = new Intent();
        ComponentName cm = new ComponentName("com.android.phone", "com.android.phone.CallFeaturesSetting");
        intent.setComponent(cm);
        return intent;
    }

    public static Intent getContactsSetting() {
        Intent intent = new Intent();
        String cls = "com.android.contacts.preference.ContactsPreferenceActivity";
        if (RomUtils.isOppo()) {
            cls = "com.oppo.contacts.activities.OppoFunctionsActivity";
        }
        ComponentName cm = new ComponentName("com.android.contacts", cls);
        intent.setComponent(cm);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static Intent insertContactsIntent(final String phone, final String name, int phoneType) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.dir/person");
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.setDataAndType(ContactsContract.Contacts.CONTENT_URI, ContactsContract.RawContacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, phoneType);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
        return intent;
    }

    public static Intent insertContactsIntent(final String phone, final String name, int phoneType, final String secondPhone, final int secondPhoneType) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.dir/person");
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.setDataAndType(ContactsContract.Contacts.CONTENT_URI, ContactsContract.RawContacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, phoneType);
        intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, secondPhone);
        intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE, secondPhoneType);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, name);

        return intent;
    }

    public static Intent insertContactsIntent(final String phone, final String name, int phoneType,
                                              final String secondPhone, final int secondPhoneType,
                                              final String tertiaryPhone, final int tertiaryPhoneType) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.dir/person");
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.setDataAndType(ContactsContract.Contacts.CONTENT_URI, ContactsContract.RawContacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, phoneType);
        intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, secondPhone);
        intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE, secondPhoneType);
        intent.putExtra(ContactsContract.Intents.Insert.TERTIARY_PHONE, tertiaryPhone);
        intent.putExtra(ContactsContract.Intents.Insert.TERTIARY_PHONE_TYPE, tertiaryPhoneType);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, name);

        return intent;
    }

}
