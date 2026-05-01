package com.colman.reread.base

import com.colman.reread.model.User

typealias UserCompletion = (User?) -> Unit
typealias SuccessCompletion = () -> Unit
typealias ErrorCompletion = (String) -> Unit
