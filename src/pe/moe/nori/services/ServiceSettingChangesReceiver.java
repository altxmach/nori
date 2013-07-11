package pe.moe.nori.services;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import pe.moe.nori.providers.ServiceSettingsProvider;

/** Broadcast receiver for updating the Service setting database with changes. */
public class ServiceSettingChangesReceiver extends BroadcastReceiver {

  /** Default constructor */
  public ServiceSettingChangesReceiver() {
  }

  @Override
  public void onReceive(Context context, Intent intent) {

    if (intent.getAction().equals("pe.moe.nori.services.ResourceTypeDetectService.result")
        && intent.getIntExtra("status", -1) == ResourceTypeDetectService.STATUS_OK)
      // Insert/update (upsert) settings into database.
      upsertSettingsChange(context, intent.<ServiceSettingsProvider.ServiceSettings>getParcelableExtra("service_settings"));
    else if (intent.getAction().equals("pe.moe.nori.providers.ServiceSettingsProvider.remove"))
      // Remove given service from database.
      removeService(context, intent.getIntExtra("pe.moe.nori.Service.id", -1));
  }

  /**
   * Removes service with given ID.
   *
   * @param context   Context
   * @param serviceId ID of service to be removed.
   */
  private void removeService(Context context, int serviceId) {
    SQLiteOpenHelper dbHelper;
    SQLiteDatabase database;

    // Get database.
    dbHelper = new ServiceSettingsProvider.DatabaseOpenHelper(context);
    database = dbHelper.getWritableDatabase();

    // Remove entry from database.
    database.delete(ServiceSettingsProvider.DatabaseOpenHelper.SERVICE_SETTINGS_TABLE_NAME,
        ServiceSettingsProvider.DatabaseOpenHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(serviceId)});

    // Close database.
    database.close();

    // Notify activities that settings have changed.
    sendUpdateBroadcast(context);
  }

  /**
   * Sends a broadcast to notify activities that service settings have been changed.
   *
   * @param context Context
   */
  private void sendUpdateBroadcast(Context context) {
    final Intent broadcastIntent = new Intent("pe.moe.nori.providers.ServiceSettingsProvider.update");
    context.sendBroadcast(broadcastIntent);
  }

  /**
   * Inserts/updates (upserts) the settings database.
   *
   * @param context         Context
   * @param serviceSettings Service settings to upsert into the database.
   */
  private void upsertSettingsChange(Context context, ServiceSettingsProvider.ServiceSettings serviceSettings) {
    SQLiteOpenHelper dbHelper;
    SQLiteDatabase db;

    // Get database.
    dbHelper = new ServiceSettingsProvider.DatabaseOpenHelper(context);
    db = dbHelper.getWritableDatabase();

    // Prepare values for inseting into the database.
    ContentValues contentValues = new ContentValues();
    contentValues.put(ServiceSettingsProvider.DatabaseOpenHelper.COLUMN_NAME, serviceSettings.name);
    contentValues.put(ServiceSettingsProvider.DatabaseOpenHelper.COLUMN_API_URL, serviceSettings.apiUrl);
    // Types and subtypes.
    contentValues.put(ServiceSettingsProvider.DatabaseOpenHelper.COLUMN_TYPE, serviceSettings.type);
    contentValues.put(ServiceSettingsProvider.DatabaseOpenHelper.COLUMN_SUBTYPE, serviceSettings.subtype);
    // Authentication.
    if (serviceSettings.requiresAuthentication) {
      contentValues.put(ServiceSettingsProvider.DatabaseOpenHelper.COLUMN_REQUIRES_AUTHENTICATION, 1);
      contentValues.put(ServiceSettingsProvider.DatabaseOpenHelper.COLUMN_USERNAME, serviceSettings.username);
      contentValues.put(ServiceSettingsProvider.DatabaseOpenHelper.COLUMN_PASSPHRASE, serviceSettings.passphrase);
    } else {
      contentValues.put(ServiceSettingsProvider.DatabaseOpenHelper.COLUMN_REQUIRES_AUTHENTICATION, 0);
    }

    // Insert if ID < 0, update otherwise.
    if (serviceSettings.id < 0) {
      db.insert(ServiceSettingsProvider.DatabaseOpenHelper.SERVICE_SETTINGS_TABLE_NAME, null, contentValues);
    } else {
      db.update(ServiceSettingsProvider.DatabaseOpenHelper.SERVICE_SETTINGS_TABLE_NAME, contentValues,
          ServiceSettingsProvider.DatabaseOpenHelper.COLUMN_ID + " = ?",
          new String[]{Integer.toString(serviceSettings.id)});
    }

    // Close database.
    dbHelper.close();

    // Notify activities that settings have changed.
    sendUpdateBroadcast(context);
  }
}