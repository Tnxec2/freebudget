package de.kontranik.freebudget.service;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import de.kontranik.freebudget.database.DatabaseHelper;

/**
 * Created by denny on 16/05/2016.
 * Source: http://stackoverflow.com/questions/18322401/is-it-posible-backup-and-restore-a-database-file-in-android-non-root-devices
 */
public class BackupAndRestore {
    public static boolean importDB(Context context) throws IOException {

        File sd = Environment.getExternalStorageDirectory();
        if (sd.canWrite()) {
            File backupDB = context.getDatabasePath(DatabaseHelper.DATABASE_NAME);
            String backupDBPath = String.format("%s.bak", DatabaseHelper.DATABASE_NAME);
            File currentDB = new File(sd, backupDBPath);

            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();

            return true;
        }

        return false;
    }

    public static boolean exportDB(Context context) throws IOException {

        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();

        if (sd.canWrite()) {
            String backupDBPath = String.format("%s.bak", DatabaseHelper.DATABASE_NAME);
            File currentDB = context.getDatabasePath(DatabaseHelper.DATABASE_NAME);
            File backupDB = new File(sd, backupDBPath);

            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();

            return true;
        }

        return false;
    }
}
