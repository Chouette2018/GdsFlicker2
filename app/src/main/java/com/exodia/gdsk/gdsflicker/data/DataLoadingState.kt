package com.exodia.gdsk.gdsflicker.data

enum class Status {
    LOADING,
    SUCCESS,
    FAILED
}

enum class Scenario {
    INITIAL,
    LOAD_BEFORE,
    LOAD_AFTER
}

@Suppress("DataClassPrivateConstructor")
data class DataLoadingState (
    val status: Status,
    val msg: String? = null,
    val errorCode : Int = -1,
    val scenario: Scenario = Scenario.INITIAL
) {
    companion object {
        fun error(msg: String?, errorCode: Int = -1, scenario: Scenario = Scenario.INITIAL)
                = DataLoadingState(
            Status.FAILED,
            msg,
            errorCode,
            scenario
        )
    }
}