package com.example.groupflow.ui.ultrascans

import org.junit.Test
import io.mockk.mockk
import io.mockk.every
import io.mockk.verify

class UploadUltrascanActivityTest {

    private val storageMock = mockk<com.google.firebase.storage.StorageReference>(relaxed = true)
    private val databaseMock = mockk<com.google.firebase.database.DatabaseReference>(relaxed = true)

    @Test
    fun testUploadFile() {
        // Mock FirebaseStorage
        val storageInstanceMock = mockk<com.google.firebase.storage.FirebaseStorage>()
        every { storageInstanceMock.reference } returns storageMock

        // Mock FirebaseDatabase
        val dbInstanceMock = mockk<com.google.firebase.database.FirebaseDatabase>()
        every { dbInstanceMock.getReference("ultrascans") } returns databaseMock

        // Now you can call your upload function using the mocks
        // For example:
        // activity.uploadFileToFirebase(uri)
        // Then verify that the mocks were called correctly
        verify { storageMock.putFile(any()) }
        verify { databaseMock.child(any()).setValue(any()) }
    }
    //(Android Developers, n.d.)
}