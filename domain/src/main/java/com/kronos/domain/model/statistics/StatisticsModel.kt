package com.kronos.domain.model.statistics

import java.io.Serializable

data class StatisticsModel(
    var id: Int = 0,
    var added: Int = 0,
    var addedLastMonth: Int = 0,
    var archived: Int = 0,
    var inTransit: Int = 0,
    var received: Int = 0,
    var moneyExpended: Double = 0.0,
    var moneyExpendedLastMonth: Double = 0.0,
):Serializable
