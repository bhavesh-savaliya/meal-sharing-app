/**Created By Nirali Pandya
 * Date :2024-03-18
 * Time :1:29 p.m.
 * Project Name :meal-sharing-app
 *
 */
package com.capstone.group6.feature_meal.data.data_source_meal
import androidx.room.TypeConverter
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


object ReferenceConverter {

    @TypeConverter
    fun documentReferenceToString(documentReference: DocumentReference?): String? {
        return documentReference?.path
    }

    @TypeConverter
    fun stringToDocumentReference(path: String?): DocumentReference? {
        return if (path.isNullOrEmpty()) {
            null
        } else FirebaseFirestore.getInstance().document(path)
    }
}