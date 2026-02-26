package com.example.licenta.viewmodel

import com.example.licenta.data.Report

sealed class ReportsState {

    object Loading : ReportsState()
    data class Success(val reports: List<Report>) : ReportsState()
    data class Error(val message: String) : ReportsState()
}