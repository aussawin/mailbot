package com.egco.mailbot.exception

class DuolicateUsernameOrEmailException(message: String = "Duplicated data",
                                        val field: String,
                                        val fieldDetail: String) : Exception(message)