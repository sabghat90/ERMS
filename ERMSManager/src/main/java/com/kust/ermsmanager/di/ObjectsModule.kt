package com.kust.ermsmanager.di

import android.app.Dialog
import android.content.Context
import com.kust.ermslibrary.models.Complaint
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.models.Event
import com.kust.ermslibrary.models.Task
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
object ObjectsModule {

    @Provides
    @FragmentScoped
    fun provideEmployeeObject(): Employee {
        return Employee()
    }

    @Provides
    @FragmentScoped
    fun provideTaskObject(): Task {
        return Task()
    }

    @Provides
    @FragmentScoped
    fun provideEventObject(): Event {
        return Event()
    }

    @Provides
    @FragmentScoped
    fun provideComplaintObject(): Complaint {
        return Complaint()
    }

    @Provides
    @FragmentScoped
    fun provideDialogObject(context: Context): Dialog {
        return Dialog(context)
    }
}