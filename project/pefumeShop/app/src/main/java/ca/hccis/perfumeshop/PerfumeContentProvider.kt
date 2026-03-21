package ca.hccis.perfumeshop

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

class PerfumeContentProvider : ContentProvider() {

    // This is the "address" other apps use to find your database
    companion object {
        const val AUTHORITY = "ca.hccis.perfumeshop.provider"
        val URI_TRANSACTIONS: Uri = Uri.parse("content://$AUTHORITY/transactions")

        private const val CODE_TRANSACTIONS = 1
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "transactions", CODE_TRANSACTIONS)
        }
    }

    override fun onCreate(): Boolean {
        return true // Provider successfully loaded
    }

    // The main function: Other apps call this to GET data
    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        context?.let { ctx ->
            // If the app asks for the correct address...
            if (uriMatcher.match(uri) == CODE_TRANSACTIONS) {
                // Open our Room Database and hand them the Cursor!
                val db = DatabaseProvider.getDatabase(ctx)
                val cursor = db.perfumeDao().getTransactionsCursor()

                // Tells the cursor to update if the database changes
                cursor.setNotificationUri(ctx.contentResolver, uri)
                return cursor
            }
        }
        return null
    }

    // --- REQUIRED BOILERPLATE: We leave these blank because we only want other apps to READ our data, not edit it. ---
    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int = 0
}