package com.unisza.fiknavigator.data.db

import androidx.room.Dao
import androidx.room.Query

@Dao
interface BuildingDao {
    @Query("SELECT building_code FROM building WHERE building_id = :buildingId")
    fun getBuildingCodeWithNodeId(buildingId: Int): String

    @Query("SELECT building_id FROM building WHERE building_code = :buildingCode")
    fun getBuildingIdWithBuildingCode(buildingCode: String?): Int
}