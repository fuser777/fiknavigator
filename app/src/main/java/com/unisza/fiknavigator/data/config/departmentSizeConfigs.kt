package com.unisza.fiknavigator.data.config

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unisza.fiknavigator.data.model.SizeConfig

// Might need to add in database in future as each map might need different marker size
val departmentSizeConfigs = mapOf(
    "FIK" to SizeConfig(portraitFontSize = 5.sp, portraitWidth = 30.dp, landscapeFontSize = 10.sp, landscapeWidth = 60.dp),
    "KAP" to SizeConfig(portraitFontSize = 10.sp, portraitWidth = 60.dp, landscapeFontSize = 12.sp, landscapeWidth = 75.dp),
    "default" to SizeConfig(portraitFontSize = 5.sp, portraitWidth = 30.dp, landscapeFontSize = 10.sp, landscapeWidth = 60.dp)
)