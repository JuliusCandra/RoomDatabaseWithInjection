package com.dev_candra.todolistapp

import android.app.Application
import com.dev_candra.todolistapp.di.ApplicationScope
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ToDoApplication : Application(){}