package com.bedirhandag.harcapaylas.util

import com.bedirhandag.harcapaylas.model.ReportModel

interface AddReportCompleteListener {
    fun isCompleted(reportModel: ReportModel)
}